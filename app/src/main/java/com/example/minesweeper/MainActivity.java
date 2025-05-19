package com.example.minesweeper;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int[][] gameState = new int[9][9];
    private int[][] revealState = new int[9][9];
    private int progress = 0;
    private boolean gameActive = true;

    public void InitialiseGame(){
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                gameState[i][j] = 0;
                revealState[i][j] = 0; // not revealed
            }
        }
        Random rand = new Random();
        for(int i=0; i<10; i++) {
            int x = rand.nextInt(9);
            int y = rand.nextInt(9);
            if(gameState[x][y] == 9){
                // For mine
                i--;
                continue;
            }
            else{
                gameState[x][y] = 9;
            }
            // These operations are for count of number of mines
            if(gameState[x][(y+1)%9]!=9){
                gameState[x][(y+1)%9] += 1;
            }
            if(gameState[x][(y-1+9)%9]!=9){
                gameState[x][(y-1+9)%9] += 1;
            }
            if(gameState[(x+1)%9][y]!=9){
                gameState[(x+1)%9][y] += 1;
            }
            if(gameState[(x-1+9)%9][y]!=9){
                gameState[(x-1+9)%9][y]+= 1;
            }
        }
//        for(int i=0; i<9; i++){
//            for(int j=0; j<9; j++){
//                System.out.printf("%d ",gameState[i][j]);
//            }
//            System.out.println();
//        }
    }
    public void gameReset(View view){
        gameActive = true;
        progress = 0;
        ((ProgressBar)findViewById(R.id.progressBar)).setProgress(0);
        ((TextView)findViewById(R.id.status)).setText("");
        ((TextView)findViewById(R.id.status)).setTextColor(getResources().getColor(R.color.purple));
        InitialiseGame();
//        ((ImageView)findViewById(R.id.imageView0)).setImageResource(0);
        for(int i=0; i<81; i++){
            String imageId = "imageView"+i;
            int resID = getResources().getIdentifier(imageId, "id", getPackageName());
            ((ImageView)findViewById(resID)).setImageResource(0);
        }
    }

    public void revealAll(View view){
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                String imageId = "imageView"+(i*9+j);
                String file = "l" + gameState[i][j];
                int resourceId = getResources().getIdentifier(file,"drawable",getPackageName());
                int imageresId = getResources().getIdentifier(imageId,"id",getPackageName());
                ((ImageView)findViewById(imageresId)).setImageResource(resourceId);
            }
        }
    }
    public void playerTap(View view){
        ImageView img = (ImageView) view;
        if(!gameActive){
            gameReset(view);
            return;
        }
        String[] tappedImage = img.getTag().toString().split(" ");
        int tapX = Integer.parseInt(tappedImage[0]);
        int tapY = Integer.parseInt(tappedImage[1]);
        if(revealState[tapX][tapY] == 0 && gameState[tapX][tapY] != 9){
            String file = "l" + gameState[tapX][tapY];
            int resourceId = getResources().getIdentifier(file, "drawable", getPackageName());
            img.setImageResource(resourceId);
            revealState[tapX][tapY] = 1; // already viewed
            progress += 1;
            int percentage = progress*100/71;
            ((ProgressBar)findViewById(R.id.progressBar)).setProgress(percentage,true);
        }
        if(gameState[tapX][tapY] == 9){
            // Mind trigerred : game lost
            img.setImageResource(R.drawable.l9);
            revealAll(view);
            ((TextView)findViewById(R.id.status)).setText("You Lost!");
            ((TextView)findViewById(R.id.status)).setTextColor(getResources().getColor(R.color.red));
            gameActive = false;
        }
        if(progress == 81-10){
            // Game won
            revealAll(view);
            ((TextView)findViewById(R.id.status)).setText("You Won!");
            ((TextView)findViewById(R.id.status)).setTextColor(getResources().getColor(R.color.green));
            gameActive = false;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InitialiseGame();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}