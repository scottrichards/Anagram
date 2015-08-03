package com.example.scottrichards.anagram;

import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    ArrayList<String> dictionary;   // list of valid words from Dictionary.txt file
    ListView anagramListView;       // list view to populate anagram matches

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readDictionary();
        anagramListView = (ListView)findViewById(R.id.listView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // invoked when click on the Find Anagrams button, create permutations and search for them in the dictionary of valid words
    public void onFindAnagrams(View view)
    {
        EditText editText = (EditText)findViewById(R.id.editText);
        String inputWord = editText.getText().toString();
        Permutations permutations = findAnagrams(inputWord);
        permutations.output();
        List<String> matchingAnagrams = permutations.realWords(dictionary, inputWord);
        outputList(matchingAnagrams);
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, matchingAnagrams);
        // Set The Adapter
        anagramListView.setAdapter(arrayAdapter);
    }

    // reads in the dictionary a text file off valid words to look up anagrams into an Arraylist
    private void readDictionary()
    {

        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(getAssets().open("Dictionary.txt")));
            dictionary = new ArrayList<>();
            String word;
            try {
                while ((word = buffer.readLine())!=null) {
                    Log.d("Main",word);
                    dictionary.add(word);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create a list of all permutations of the inputWord
    private Permutations findAnagrams(String inputWord)
    {
        if (inputWord.length() > 0) {
            hideSoftKeyBoard();
            Permutations permutationsList = new Permutations();
            for (int i = 0; i < inputWord.length(); i++) {
                permutationsList.addLetterToPermutations(inputWord.charAt(i));
            }
            return permutationsList;
        } else
            return null;
    }

    // class for figuring out all of the permutations of a string
    class Permutations {

        public List<String> permutationList;

        // add the letter to list of permutations
        public void addLetterToPermutations(char letter) {
            if (this.permutationList == null) {
                this.permutationList = new ArrayList<String>();
                this.permutationList.add(String.valueOf(letter));
            } else {
                int oldCount = this.permutationList.size();
                List<String> newPermutationList = new ArrayList<String>();
                // iterate over previous permutations
                for (int index=0; index < this.permutationList.size(); index++) {
                    String newPermutation = new String();
                    String oldPermutation = permutationList.get(index);
                    // add letter at every location in the old permutation
                    for (int j=0; j < (oldPermutation.length() + 1);j++) {
                        newPermutation = insertAtIndex(oldPermutation, j, letter);
                        newPermutationList.add(newPermutation);
                    }
                }
                this.permutationList = newPermutationList;
            }
        }

        // return list of real words from the permutation list
        public ArrayList<String>realWords(ArrayList<String> dictionary,String inputWord)
        {
            ArrayList<String> realWordList = new ArrayList<String>();
            for (String permutation : permutationList) {
                // if it is not equal to the input Word and it exists in the dictionary add it
                if (!(inputWord.equals(permutation)) && dictionary.contains(permutation)) {
                    realWordList.add(permutation);
                }
            }
            return realWordList;
        }

        // debug output
        public void output()
        {
            for (String permutation : permutationList) {
                Log.d("output",permutation);
            }
        }
    }

    // debug output all items in List
    public void outputList(List<String> list)
    {
        Log.d("outputList","-- OUTPUT LIST: ");
        for (String item : list){
            Log.d("outputList",item);
        }
        Log.d("outputList","-- OUTPUT LIST");
    }

    // inserts a character at the specified index of the string, returns new string with inserted character
    // input -> string to insert the character into
    // index -> location to the insert character
    // character -> characer to be inserted
    public String insertAtIndex(String input,int index,char character) {
        String newString = input.substring(0,index);
        newString += character;
        newString += input.substring(index,input.length());
        return newString;
    }

    // dismiss the soft keyboard
    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
