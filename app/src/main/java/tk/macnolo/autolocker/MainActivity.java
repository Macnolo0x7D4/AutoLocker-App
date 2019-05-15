package tk.macnolo.autolocker;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import java.io.IOException;
import java.util.UUID;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    Button openClose, connect;
    EditText password;
    String address = null;
    String writtenPassword;
    static final String correctPassword1 = "fd272fe04b7d4e68effd01bddcc6bb34";
    static final String correctPassword2 = "451b7ed3a3f81564a51f3b904e345406";
    TextView msgLabel;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private boolean ConnectSuccess = true;
    BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        address = "C2:2C:10:04:0A:96";
        setContentView(R.layout.activity_main);
        openClose = findViewById(R.id.openClose);
        connect = findViewById(R.id.connect);
        password = findViewById(R.id.password);
        msgLabel = findViewById(R.id.msgLabel);

        openClose.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                if(isBtConnected) {
                    writtenPassword = getMD5(password.getText().toString());
                    if (writtenPassword.equals(correctPassword1) || writtenPassword.equals(correctPassword2)) {
                        msgLabel.setText("Contraseña correcta. Enviando comando.");
                        if (btSocket != null) {
                            try {
                                btSocket.getOutputStream().write(writtenPassword.getBytes());
                            } catch (IOException e) {
                                msg("Error");
                            }
                        }
                    } else {
                        msgLabel.setText("Contraseña incorrecta.");
                    }
                }else{
                    msg("Conectate primero al AutoLocker");
                }
            }
        });

        connect.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                if (isBtConnected)
                {
                    try {
                        btSocket.close();
                        msg("Desconectado");
                        connect.setText("Conectar");
                        isBtConnected = false;
                    } catch (IOException e) {
                        msg("Error");
                    }
                } else {
                    try {
                        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                        btSocket.connect();
                        connect.setText("Desconectar");
                        isBtConnected = true;
                    } catch (IOException e) {
                        ConnectSuccess = false;
                    }
                    if (!ConnectSuccess) {
                        msg("Conexion fallida.");
                        isBtConnected = false;
                    } else {
                        msg("Conectado al AutoLocker");
                        isBtConnected = true;
                    }
                }
            }
        });
    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(number.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}