package clarkson.ee408.tictactoev4;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import clarkson.ee408.tictactoev4.client.SocketClient;
import clarkson.ee408.tictactoev4.socket.Request;
import clarkson.ee408.tictactoev4.client.AppExecutors;
import clarkson.ee408.tictactoev4.socket.GamingResponse;
import clarkson.ee408.tictactoev4.socket.Response;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private TicTacToe tttGame;
    private Button [][] buttons;
    private TextView status;
    private Gson gson;
    private Handler moveRequestHandler;
    private boolean shouldRequestMove = false;
    private static final long REQUEST_MOVE_INTERVAL = 3000;
    private SocketClient socketClient;
    private AppExecutors appExecutors;



    public void abortGame(final Context context) {
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                // Assume sendAbortGameRequest is a method in SocketClient
                boolean success = socketClient.sendRequest(Request.RequestType.ABORT_GAME);

                // Display a toast based on the success of the request
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(context, "ABORT_GAME request sent successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to send ABORT_GAME request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void sendMove(int move) {
        // Create a Request object with the type SEND_MOVE
        Request request = new Request(Request.RequestType.SEND_MOVE, serializeMove(move));


        // Use the AppExecutors to send the request in the networkIO thread
        AppExecutors.getInstance().networkIO().execute(() -> {
            // Send the request using the SocketClient
            SocketClient socketClient = SocketClient.getInstance();
            Response response = socketClient.sendRequest(request, Response.class);
            AppExecutors.getInstance().mainThread().execute(() -> {
                if(response != null && response.getStatus() == Response.ResponseStatus.SUCCESS) {
                    Log.e("SEND", "Move " + move + " Sent");
                }else{
                    Log.e("SEND", "Move " + move + " Not Sent");
                }
            });
        });
    }

    public String serializeMove(int move) {
        return gson.toJson(move);
    }

    public void requestMove() {
        // Create a Request object with the type REQUEST_MOVE
        Request request = new Request(Request.RequestType.REQUEST_MOVE, null);
        GamingResponse gamingResponse = new GamingResponse();
        //add if clause to see if game is active
        if (gamingResponse.isActive()== false){
            enableButtons( false );
            resetButtons( );
            status.setBackgroundColor( Color.YELLOW );
            status.setText("Response Message" );
            shouldRequestMove = false;
            updateTurnStatus();
            tttGame = null;

        }

        // Use the AppExecutors to send the request in the networkIO thread
        AppExecutors.getInstance().networkIO().execute(() -> {
            // Send the request using the SocketClient
            SocketClient socketClient = SocketClient.getInstance();
            GamingResponse response = socketClient.sendRequest(request, GamingResponse.class);
            AppExecutors.getInstance().mainThread().execute(() -> {
                if (response != null && response.getStatus() == GamingResponse.ResponseStatus.SUCCESS) {
                    int move = response.getMove();
                    int row = move /3;
                    int col = move % 3;
                    Log.e("REQUEST", "Move " + move);
                    if (isValidMove(row, col)) {
                        // Update the board with the received move in the mainThread
                        update(row, col);
                    }
                } else {
                    Log.e("REQUEST", "No Move");
                }
            });

        });
    }

    public boolean isValidMove(int row, int col) {
        // Check if the cell is within the bounds of the board
        return row >= 0 && row < 3 && col >= 0 && col < 3;// The move is invalid if it's outside the bounds or not empty
    }



    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_main);

        int playervalue = getIntent().getIntExtra("Player_Value", 1);
        tttGame = new TicTacToe(playervalue);
        buildGuiByCode();
        gson = new Gson();
        shouldRequestMove = true;
        updateTurnStatus();
        moveRequestHandler = new Handler();

        startPeriodicMoveRequest();
    }

    private void startPeriodicMoveRequest() {
        moveRequestHandler.postDelayed(() -> {
            if (shouldRequestMove) {
                requestMove();
            }

            // Schedule the next move request
            startPeriodicMoveRequest();
        }, REQUEST_MOVE_INTERVAL);
    }

    private void updateTurnStatus() {
        if (tttGame.getPlayer() == tttGame.getTurn()) {
            // It's the current player's turn
            status.setText("Your Turn");
            enableButtons(true);// Enable buttons for the current player
           // shouldRequestMove = false; task 9
        } else {
            // It's the opponent's turn
            status.setText("Waiting for Opponent");
            enableButtons(false); // Disable buttons for the opponent
           // shouldRequestMove = true; task 9
        }
    }

    public void buildGuiByCode( ) {
        // Get width of the screen
        Point size = new Point( );
        getWindowManager( ).getDefaultDisplay( ).getSize( size );
        int w = size.x / TicTacToe.SIDE;

        // Create the layout manager as a GridLayout
        GridLayout gridLayout = new GridLayout( this );
        gridLayout.setColumnCount( TicTacToe.SIDE );
        gridLayout.setRowCount( TicTacToe.SIDE + 2 );

        // Create the buttons and add them to gridLayout
        buttons = new Button[TicTacToe.SIDE][TicTacToe.SIDE];
        ButtonHandler bh = new ButtonHandler( );

//        GridLayout.LayoutParams bParams = new GridLayout.LayoutParams();
//        bParams.width = w - 10;
//        bParams.height = w -10;
//        bParams.bottomMargin = 15;
//        bParams.rightMargin = 15;

        gridLayout.setUseDefaultMargins(true);

        for( int row = 0; row < TicTacToe.SIDE; row++ ) {
            for( int col = 0; col < TicTacToe.SIDE; col++ ) {
                buttons[row][col] = new Button( this );
                buttons[row][col].setTextSize( ( int ) ( w * .2 ) );
                buttons[row][col].setOnClickListener( bh );
                GridLayout.LayoutParams bParams = new GridLayout.LayoutParams();
//                bParams.width = w - 10;
//                bParams.height = w -40;

                bParams.topMargin = 0;
                bParams.bottomMargin = 20;
                bParams.leftMargin = 0;
                bParams.rightMargin = 20;
                bParams.width=w-10;
                bParams.height=w-10;
                buttons[row][col].setLayoutParams(bParams);
                gridLayout.addView( buttons[row][col]);
//                gridLayout.addView( buttons[row][col], bParams );
            }
        }

        // set up layout parameters of 4th row of gridLayout
        status = new TextView( this );
        GridLayout.Spec rowSpec = GridLayout.spec( TicTacToe.SIDE, 2 );
        GridLayout.Spec columnSpec = GridLayout.spec( 0, TicTacToe.SIDE );
        GridLayout.LayoutParams lpStatus
                = new GridLayout.LayoutParams( rowSpec, columnSpec );
        status.setLayoutParams( lpStatus );

        // set up status' characteristics
        status.setWidth( TicTacToe.SIDE * w );
        status.setHeight( w*2 );
        status.setGravity( Gravity.CENTER );
        status.setBackgroundColor( Color.BLUE );
        status.setTextSize( ( int ) ( w * .15 ) );
        status.setText( tttGame.result( ) );

        gridLayout.addView( status );

        // Set gridLayout as the View of this Activity
        setContentView( gridLayout );
    }

    public void update( int row, int col ) {
        int play = tttGame.play( row, col );
        if( play == 1 )
            buttons[row][col].setText( "X" );
        else if( play == 2 )
            buttons[row][col].setText( "O" );
        if( tttGame.isGameOver( ) ) {
            shouldRequestMove = false;
            status.setBackgroundColor( Color.YELLOW );
            enableButtons( false );
            status.setText( tttGame.result( ) );
            showNewGameDialog( );	// offer to play again
        }else {
            updateTurnStatus();
        }
    }

    public void enableButtons( boolean enabled ) {
        for( int row = 0; row < TicTacToe.SIDE; row++ )
            for( int col = 0; col < TicTacToe.SIDE; col++ )
                buttons[row][col].setEnabled( enabled );
    }

    public void resetButtons( ) {
        for( int row = 0; row < TicTacToe.SIDE; row++ )
            for( int col = 0; col < TicTacToe.SIDE; col++ )
                buttons[row][col].setText( "" );
    }

    public void showNewGameDialog( ) {
        AlertDialog.Builder alert = new AlertDialog.Builder( this );
        alert.setTitle(tttGame.result());

        // Change the message of the dialog to "Do you want to play again?"
        alert.setMessage("Do you want to play again?");
        PlayDialog playAgain = new PlayDialog( );
        alert.setPositiveButton( "YES", playAgain );
        alert.setNegativeButton( "NO", playAgain );
        alert.show( );

    }

    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v ) {
            Log.d("button clicked", "button clicked");

            for( int row = 0; row < TicTacToe.SIDE; row ++ )
                for( int column = 0; column < TicTacToe.SIDE; column++ )
                    if( v == buttons[row][column] ) {
                        int move = (row*3)+column;
                        sendMove(move);
                        update(row, column);
                    }
        }
    }

    private class PlayDialog implements DialogInterface.OnClickListener {
        public void onClick( DialogInterface dialog, int id ) {
            if( id == -1 ) /* YES button */ {
                tttGame.resetGame( );
                shouldRequestMove = true;
                // If the game was a tie, switch the starting player
                if (tttGame.getPlayer() == 1) {
                    tttGame.setPlayer(2);
                } else {
                    tttGame.setPlayer(1);
                }
                enableButtons( true );
                resetButtons( );
                status.setBackgroundColor( Color.YELLOW );
                status.setText( tttGame.result( ) );
                updateTurnStatus();
            }
            else if( id == -2 ) // NO button
                MainActivity.this.finish( );
        }
    }
}