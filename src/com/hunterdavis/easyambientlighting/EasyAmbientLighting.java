package com.hunterdavis.easyambientlighting;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.hunterdavis.easyambientlighting.ColorPickerDialog.OnColorChangedListener;

public class EasyAmbientLighting extends Activity {
	
	
	int SELECT_PICTURE = 22;
	ImageView currentlySelectedImage;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        // load image button
		OnClickListener imageListener = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				// in onCreate or any event where your want the user to
				// select a file
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Source Photo"),
						SELECT_PICTURE);
				}

			};
		
		
		Button loadbutton = (Button) findViewById(R.id.loadButton);
		loadbutton.setOnClickListener(imageListener);
        
        
        // pick color button
		// listener for frequency button
		OnClickListener colorListener = new OnClickListener() {
			public void onClick(View v) {
					ImageView imageOne = (ImageView) findViewById(R.id.ImageViewResult);
					colorPicker(imageOne, v.getContext());
				}

			};
		
		
		Button mycolorbutton = (Button) findViewById(R.id.colorButton);
		mycolorbutton.setOnClickListener(colorListener);
		
		
		
		
		
        
        
		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
    }// end of oncreate
    
    
	public void colorPicker(ImageView image, Context context) {
		// initialColor is the initially-selected color to be shown in the
		// rectangle on the left of the arrow.
		// for example, 0xff000000 is black, 0xff0000ff is blue. Please be aware
		// of the initial 0xff which is the alpha.
		currentlySelectedImage = image;
		
		OnColorChangedListener ourchangelistener = new OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				// TODO Auto-generated method stub
				genColor(currentlySelectedImage, color);
			}
		};
		new ColorPickerDialog(context, ourchangelistener, 333444).show();

	}
	
	public Boolean genColor(ImageView imgview, int Color) {

		// create a width*height long int array and populate it with random 1 or
		// 0
		// final Random myRandom = new Random();
		int imageWidth = imgview.getWidth();
		int imageHeight = imgview.getHeight();
		int rgbSize = imageWidth * imageHeight;
		int[] rgbValues = new int[rgbSize];
		for (int i = 0; i < rgbSize; i++) {
			rgbValues[i] = Color;
		}

		// create a width*height bitmap
		Bitmap staticBitmap = Bitmap.createBitmap(rgbValues, imageWidth, imageHeight,
				Bitmap.Config.RGB_565);

		// set the imageview to the static
		imgview.setImageBitmap(staticBitmap);

		return true;

	}
    
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				ImageView imgView = (ImageView) findViewById(R.id.ImageViewResult);
				Boolean scaleDisplay = scaleURIAndDisplay(getBaseContext(),
						selectedImageUri, imgView);
			}
		}
	}
    
	
	public Boolean scaleURIAndDisplay(Context context, Uri uri,
			ImageView imgview) {
		double divisorDouble = 400;
		InputStream photoStream = null;
		
		try {
			photoStream = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;

		Bitmap photoBitmap = BitmapFactory.decodeStream(photoStream, null, options);
		if (photoBitmap == null) {
			return false;
		}
		int h = photoBitmap.getHeight();
		int w = photoBitmap.getWidth();
		
		
		long redtotal = 0;
		long greentotal = 0;
		long bluetotal = 0;
		int currentItem = 0;
		int numItems = 0;
		
		
		// This is gonna take up some time....
		for(int i = 0;i<h;i++)
		{
			for(int j = 0;j<w;j++)
			{
				numItems++;
				currentItem = photoBitmap.getPixel(j,i);
				redtotal += Color.red(currentItem);
				greentotal += Color.green(currentItem);
				bluetotal += Color.blue(currentItem);
				
			}
		}
		int red = (int) redtotal/numItems;
		int green = (int) greentotal/numItems;
		int blue = (int) bluetotal/numItems;
		int alpha = 0;
		int packedColor = Color.rgb(red, green, blue);
		genColor(imgview, packedColor);
		
		return true;
	}    
	
    
}// end of file