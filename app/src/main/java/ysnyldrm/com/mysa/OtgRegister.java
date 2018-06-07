package ysnyldrm.com.mysa;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
public class OtgRegister extends AppCompatActivity {

    private SecureRandom random = new SecureRandom();
    SqliteHelper2 sqliteHelper2;

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    ImageView imageView1;
    ImageView imageView2;

    String vendorid ;
    String productid;
    String gd;
    String randomstr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otg_register);
        sqliteHelper2 = new SqliteHelper2(this);



        textView1 = (TextView) findViewById(R.id.textview1);
        textView2 = (TextView) findViewById(R.id.textview2);
        textView3 = (TextView) findViewById(R.id.textview3);
        textView4 = (TextView) findViewById(R.id.textview4);
        imageView1 = (ImageView) findViewById(R.id.imageview1);
        imageView2 = (ImageView) findViewById(R.id.imageview2);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter);
        discoverDevice();

        finalprocess();



    }

    public void finalprocess (){


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent intent = new Intent(OtgRegister.this,FingerprintActivity.class);
                startActivity(intent);

            }

        }, 10000);


    }

    /**
     * Action string to request the permission to communicate with an UsbDevice.
     */

    private static final String ACTION_USB_PERMISSION = "com.github.mjdev.libaums.USB_PERMISSION";

    private UsbMassStorageDevice device;

    private FileSystem fs;

    private  void runAll() {

        try {
            //in order to setup usb
            CreateFile();
            sqliteHelper2.addUser(new User2(null, "vendor1", "product1", gd, randomstr));

            if (sqliteHelper2.isOTGexists("1") == true) {
                textView1.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.GONE);
                textView4.setVisibility(View.GONE);
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.GONE);





            } else {

                textView1.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                textView3.setVisibility(View.VISIBLE);
                textView4.setVisibility(View.VISIBLE);
                imageView1.setVisibility(View.GONE);
                imageView2.setVisibility(View.VISIBLE);


            }

            //in order to validate usb
            // checkAndReadFile();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private  void testCreateFile()
            throws IOException {

        UsbFile[] files;

        UsbFile root = fs.getRootDirectory();

        UsbFile testDirectory = root.search("testCreateFile");
        if(testDirectory != null) {
            testDirectory.delete();
        }

        testDirectory = root.createDirectory("testCreateFile");
        files = testDirectory.listFiles();
        assertTrue((files != null) && (files.length == 0));

        testDirectory.createDirectory("testFile1");

        testDirectory.createDirectory("testFile2");

        files = testDirectory.listFiles();
        assertTrue((files != null) && (files.length == 2));

        //testDirectory.delete();

    }

    private  void CreateFile()
            throws IOException {

        UUID uid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        String guid = uid.randomUUID().toString();
        String randStr = nextSessionId();
        UsbFile[] files;

        UsbFile root = fs.getRootDirectory();

        UsbFile file = root.createFile(guid + ".mysa");
        OutputStream os = new UsbFileOutputStream(file);

        vendorid = "24a";
        productid = "xc21";
        gd = guid;
        randomstr = randStr;

        os.write(randStr.getBytes());
        os.close();
    }


    // Creating random string in order to write file
    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }


    private  void discoverDevice() {

        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(this);

        // we only use the first device
        device = devices[0];

        UsbDevice usbDevice = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);

        if (usbDevice != null && usbManager.hasPermission(usbDevice)) {

            Toast.makeText(this, (String)"received usb device via intent",
                    Toast.LENGTH_LONG).show();
            // requesting permission is not needed in this case
            setupDevice();

        } else {

            // first request permission from user to communicate with the
            // underlying
            // UsbDevice
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                    ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(device.getUsbDevice(), permissionIntent);

        }



    }

    /**
     * Sets the device up and shows the contents of the root directory.
     */
    private  void setupDevice() {

        try {

            device.init();

            fs = device.getPartitions().get(0).getFileSystem();

            runAll();

        } catch (IOException e) {
        }

    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public  void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {

                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                    if (device != null) {
                        setupDevice();
                    }

                }

            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);


                // determine if connected device is a mass storage device
                if (device != null) {
                    discoverDevice();
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);


                // determine if connected device is a mass storage device
                if (device != null) {
                    if (OtgRegister.this.device != null) {
                        OtgRegister.this.device.close();
                    }
                    // check if there are other devices or set action bar title
                    // to no device if not
                    discoverDevice();
                }

            }

        }
    };


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure for exit the application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }
}