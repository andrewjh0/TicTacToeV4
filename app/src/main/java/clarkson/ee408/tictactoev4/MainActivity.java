package clarkson.ee408.tictactoev4;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import client.SocketClient;
import socket.Request;
import client.AppExecutors;
import socket.GamingResponse;
import socket.Response;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private TicTacToe tttGame;
    private Button [][] buttons;
    private TextView status;
    private Gson gson;
    private Handler moveRequestHandler;
    private boolean shouldRequestMove = false;
    private static final long REQUEST_MOVE_INTERVAL = 1000;



    public void sendMove(int move) {
        // Create a Request object with the type SEND_MOVE
        Request request = new Request(Request.RequestType.SEND_MOVE);

        // Serialize the move and set it as the data attribute of the request
        String serializedMove = serializeMove(move);
        request.setData(serializedMove);

        // Use the AppExecutors to send the request in the networkIO thread
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                // Send the request using the SocketClient
                SocketClient socketClient = SocketClient.getInstance();
                socketClient.send(request);

            }
        });
    }

    public String serializeMove(int move) {
        return Integer.toString(move);
    }

    public void requestMove(final int row, final int col) {
        // Create a Request object with the type REQUEST_MOVE
        Request request = new Request(Request.RequestType.REQUEST_MOVE);

        // Use the AppExecutors to send the request in the networkIO thread
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                // Send the request using the SocketClient
                SocketClient socketClient = SocketClient.getInstance();
                socketClient.send(request);

                // Receive the response (assuming it's a GamingResponse)
                GamingResponse response = (GamingResponse) socketClient.receive();

                if (response != null && response.getStatus() == Response.ResponseStatus.SUCCESS) {
                    int move = response.getMove();
                    if (isValidMove(row, col)) {
                        // Update the board with the received move in the mainThread
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                update(row, col);
                            }
                        });
                    }
                }
            }
        });
    }

    public boolean isValidMove(int row, int col) {
        // Check if the cell is within the bounds of the board
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            return true;
        }
        return false; // The move is invalid if it's outside the bounds or not empty
    }



    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_main);


        tttGame = new TicTacToe( );
        buildGuiByCode( );
        gson = new Gson();
        updateTurnStatus();
        moveRequestHandler = new Handler();

        // Start periodic requests if it's not the current player's turn
        if (tttGame.getPlayer() != tttGame.getTurn()) {
            shouldRequestMove = true;
            startPeriodicMoveRequest();
        }
    }

    private void startPeriodicMoveRequest() {
        moveRequestHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (shouldRequestMove) {
                    // Send the request for the other player's move to the server here
                    // You can make a network call to request the move

                    // After the request, set the flag to false if it's the current player's turn
                    if (tttGame.getPlayer() == tttGame.getTurn()) {
                        shouldRequestMove = false;
                    }
                }

                // Schedule the next move request
                startPeriodicMoveRequest();
            }
        }, REQUEST_MOVE_INTERVAL);
    }

    private void updateTurnStatus() {
        if (tttGame.getPlayer() == tttGame.getTurn()) {
            // It's the current player's turn
            status.setText("Your Turn");
            enableButtons(true);// Enable buttons for the current player
            shouldRequestMove = false;
        } else {
            // It's the opponent's turn
            status.setText("Waiting for Opponent");
            enableButtons(false); // Disable buttons for the opponent
            shouldRequestMove = true;
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
            buttons[row][col].setText( "+" );
        else if( play == 2 )
            buttons[row][col].setText( "-" );
        if( tttGame.isGameOver( ) ) {
            status.setBackgroundColor( Color.YELLOW );
            enableButtons( false );
            status.setText( tttGame.result( ) );
            showNewGameDialog( );	// offer to play again
        }
        updateTurnStatus();
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

        if (tttGame.result().equals("Tie Game")) {
            // If the game was a tie, switch the starting player
            if (tttGame.getPlayer() == 1) {
                tttGame.setPlayer(2);
            } else {
                tttGame.setPlayer(1);
            }
        }
        updateTurnStatus();
    }

    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v ) {
            Log.d("button clicked", "button clicked");

            for( int row = 0; row < TicTacToe.SIDE; row ++ )
                for( int column = 0; column < TicTacToe.SIDE; column++ )
                    if( v == buttons[row][column] )
                        update( row, column );
        }
    }

    private class PlayDialog implements DialogInterface.OnClickListener {
        public void onClick( DialogInterface dialog, int id ) {
            if( id == -1 ) /* YES button */ {
                tttGame.resetGame( );
                enableButtons( true );
                resetButtons( );
                status.setBackgroundColor( Color.YELLOW );
                status.setText( tttGame.result( ) );
            }
            else if( id == -2 ) // NO button
                MainActivity.this.finish( );
        }
    }
}