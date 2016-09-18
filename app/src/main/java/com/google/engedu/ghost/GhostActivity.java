package com.google.engedu.ghost;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private Button challenge,restart;
    public TextView ghostText,gameStatus;
    public static boolean user_started_first;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        ghostText=(TextView)findViewById(R.id.ghostText);
        gameStatus=(TextView)findViewById(R.id.gameStatus);
        challenge=(Button)findViewById(R.id.challengebutton);
        restart=(Button)findViewById(R.id.restartbutton);
       try{
           InputStream ins=getAssets().open("words.txt");
           dictionary=new SimpleDictionary(ins);
       }catch (Exception e)
       {
           Toast toast=Toast.makeText(this,"Could not load dictionary",Toast.LENGTH_LONG);
           Log.e("Yash in ghostActivity","Error Bro");
       }
        onStart(null);
        challenge.setOnClickListener(this);
        restart.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_ghost, menu);
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

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        String cur_fragment=ghostText.getText().toString();
        if(dictionary.isWord(cur_fragment) && cur_fragment.length()>=4)
        {
        label.setText("Computer Wins");
            return ;
        }
        else
        {
            String word=dictionary.getGoodWordStartingWith(ghostText.getText().toString());
            //String word=dictionary.getAnyWordStartingWith(ghostText.getText().toString());
            if(word==null)
            {
                label.setText("No word starts with this prefix.Computer Wins!");
                return ;
            }
            else {
                String new_word = ghostText.getText().toString()+word.charAt(ghostText.getText().toString().length());
                ghostText.setText(new_word);
            }
        }
        // Do computer turn stuff then make it the user's turn again
        userTurn = true;
        label.setText(USER_TURN);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            user_started_first=true;
            label.setText(USER_TURN);
        } else {
            user_started_first=false;
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.challengebutton:
                String curr_fragment=ghostText.getText().toString();
                if(curr_fragment.length()>=4 && dictionary.isWord(curr_fragment))
                {

                    gameStatus.setText("User wins as it a word");
                }
                else
                {
                    String word=dictionary.getAnyWordStartingWith(curr_fragment);
                    if(word==null)
                    {
                        gameStatus.setText("User wins as no word is possible");

                    }else
                    {
                        gameStatus.setText("Computer wins as the word possible is :"+word);
                    }
                }
                    break;
            case R.id.restartbutton:
                onStart(null);
                break;
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        int pressed_key_ascii=event.getUnicodeChar();
        char pressed_key_character=(char)pressed_key_ascii;
        String pressed_character= ""+ pressed_key_character;
        if((pressed_key_ascii>=65 && pressed_key_ascii <=90)|| (pressed_key_ascii >=90 && pressed_key_ascii<=122))
        {
            ghostText.setText(ghostText.getText() + pressed_character);
            //computerTurn();
            if(dictionary.isWord(ghostText.getText().toString()))
            {
                gameStatus.setText("It is a Valid Word");
            }
            else
            {
                gameStatus.setText(COMPUTER_TURN);
                userTurn=false;

            }
        }
        computerTurn();
        return super.onKeyUp(keyCode, event);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

   /* @Override
    protected void onPause() {
        super.onPause();
        finish();
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("Current_word",ghostText.getText().toString());
        outState.putCharSequence("Current_status",gameStatus.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        CharSequence restored_word=savedInstanceState.getCharSequence("Current_word");
        CharSequence restored_game_status=savedInstanceState.getCharSequence("Current_status");
        ghostText.setText(restored_word);
        gameStatus.setText(restored_game_status);
    }

}
