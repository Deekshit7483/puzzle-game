package com.Puzzle.game;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {
	
	public final int REQ_CD_IMG = 101;
	
	private double size = 0;
	private double rndm = 0;
	private boolean newPuzzle = false;
	private HashMap<String, Object> piece = new HashMap<>();
	private double number = 0;
	private double pos = 0;
	private double wrong = 0;
	private double numb = 0;
	
	private ArrayList<HashMap<String, Object>> puzzle = new ArrayList<>();
	private ArrayList<Double> puzzled = new ArrayList<>();
	
	private LinearLayout top;
	private LinearLayout gridlayout;
	private LinearLayout holder;
	private ImageView imageview1;
	private ListView listview1;
	private Button button1;
	private TextView textview1;
	private Button button3;
	private ImageView imageview2;
	private ImageView imageview3;
	private ImageView imageview4;
	private ImageView imageview5;
	
	private Intent img = new Intent(Intent.ACTION_GET_CONTENT);
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
			} else {
				initializeLogic();
			}
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		top = findViewById(R.id.top);
		gridlayout = findViewById(R.id.gridlayout);
		holder = findViewById(R.id.holder);
		imageview1 = findViewById(R.id.imageview1);
		listview1 = findViewById(R.id.listview1);
		button1 = findViewById(R.id.button1);
		textview1 = findViewById(R.id.textview1);
		button3 = findViewById(R.id.button3);
		imageview2 = findViewById(R.id.imageview2);
		imageview3 = findViewById(R.id.imageview3);
		imageview4 = findViewById(R.id.imageview4);
		imageview5 = findViewById(R.id.imageview5);
		img.setType("image/*");
		img.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				ViewGroup parentC = (ViewGroup) holder.getParent();
				if (parentC != null) {
					parentC.removeView(holder);
				}
				AlertDialog.Builder pBuilder = new AlertDialog.Builder(MainActivity.this);
				pBuilder.setView(holder);
				final AlertDialog pDialog = pBuilder.create();
				pDialog.show();
				
				imageview2.setOnClickListener(new OnClickListener() { 
					public void onClick(View v) {
						pDialog.dismiss();
						imageview1.setImageResource(R.drawable.one);
						splitImage(imageview1, 16);
					} 
				});
				
				imageview3.setOnClickListener(new OnClickListener() { 
					public void onClick(View v) {
						pDialog.dismiss();
						imageview1.setImageResource(R.drawable.two);
						splitImage(imageview1, 16);
					} 
				});
				
				imageview4.setOnClickListener(new OnClickListener() { 
					public void onClick(View v) {
						pDialog.dismiss();
						imageview1.setImageResource(R.drawable.three);
						splitImage(imageview1, 16);
					} 
				});
				
				imageview5.setOnClickListener(new OnClickListener() { 
					public void onClick(View v) {
						pDialog.dismiss();
						imageview1.setImageResource(R.drawable.five);
						splitImage(imageview1, 16);
					} 
				});
				
			}
		});
		
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				AlertDialog.Builder kBuilder = new AlertDialog.Builder(MainActivity.this);
				final LinearLayout layout = new LinearLayout(MainActivity.this);
				layout.setGravity(Gravity.CENTER_HORIZONTAL);
				layout.setBackgroundColor(Color.TRANSPARENT);
				
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(500, 500); 
				
				final ImageView im1 = new ImageView(MainActivity.this);
				im1.setLayoutParams(lp);
				im1.setImageDrawable(imageview1.getDrawable());
				layout.addView(im1);
				
				kBuilder.setView(layout);
				final AlertDialog kDialog = kBuilder.create();
				kDialog.show();
				kDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			}
		});
	}
	
	private void initializeLogic() {
		button3.setVisibility(View.INVISIBLE);
		//get the screen size to adjust gridview for every screen
		size = Double.parseDouble(String.valueOf((long)(SketchwareUtil.getDisplayWidthPixels(getApplicationContext()) / 4)));
		//create a maplist with the same ammount of items you will have on the puzzle and a number list
		number = 0;
		for(int _repeat52 = 0; _repeat52 < (int)(16); _repeat52++) {
			piece = new HashMap<>();
			piece.put("a", "1");
			puzzle.add(piece);
			puzzled.add(Double.valueOf(number));
			number++;
		}
	}
	//declare int amd bitmap array
	private int draggedIndex = -1; 
	private int droppedIndex = -1;
	private ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>();
	{
		
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_IMG:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
				
			}
			else {
				
			}
			break;
			default:
			break;
		}
	}
	
	public void _refresh() {
		//declare and refresh the gridview 
		GridView grdvw = (GridView) findViewById(1);
		
		((BaseAdapter)grdvw.getAdapter()).notifyDataSetChanged();
		_checkwin();
	}
	
	
	public void _Dragger(final View _view) {
		//since we are workinf with images we will not apply tints to the background on any event
		
		_view.setOnDragListener( new OnDragListener(){
			 @Override public boolean onDrag(View v, DragEvent dragEvent) { switch (dragEvent.getAction()) { 
					case DragEvent.ACTION_DRAG_STARTED: 
					//check if the dragges item can be dropped here
					if(dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
						return true;
					}
					return false;
					
					case DragEvent.ACTION_DRAG_ENTERED:
					return true;
					
					case DragEvent.ACTION_DRAG_EXITED: 
					return true;
					
					case DragEvent.ACTION_DRAG_LOCATION:
					return true; 
					
					case DragEvent.ACTION_DROP:
					//declare gridview
					GridView grdvw = (GridView) findViewById(1);
					//get the position where the drop event happen
					LinearLayout container = (LinearLayout) v;
					droppedIndex = grdvw.getPositionForView(v); 
					
					//swap the position of the items om the list and refresh
					Collections.swap(puzzled, draggedIndex, droppedIndex);
					
					_refresh();
					
					draggedIndex = -1; 
					droppedIndex = -1; 
					
					case DragEvent.ACTION_DRAG_ENDED: 
					return true; 
				} 
				return false; 
			}
		});
		
		
		
		
	}
	
	
	public void _chopper() {
	}
	//split the image into smaller pieces
	private void splitImage(ImageView image, int chunkNumbers) { 
		//delete all actual items on the bitmap array
		chunkedImages.clear();
		
		int rows,cols;
		int chunkHeight,chunkWidth;
		
		image.buildDrawingCache();
		Bitmap bitmap = image.getDrawingCache();
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
		//same ammount of rows and columns
		rows = cols = (int) Math.sqrt(chunkNumbers);
		//get the size of each piece
		chunkHeight = bitmap.getHeight()/rows;
		chunkWidth = bitmap.getWidth()/cols;
		
		int num = 0;
		int yCoord = 0;
		for(int x=0; x<rows; x++){
			int xCoord = 0;
			for(int y=0; y<cols; y++){
				
				chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
				num++;
				xCoord += chunkWidth;
			}
			yCoord += chunkHeight;
		}
		//shuffle the number list and set the new puzzle
		Collections.shuffle(puzzled);
		_setPuzzle();
	}
	{
	}
	
	
	public void _setPuzzle() {
		//set the listmap to the listview
		listview1.setAdapter(new Listview1Adapter(puzzle));
		((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
		button3.setVisibility(View.VISIBLE);
		//crete the gridview based on a boolean to avoid creating more than 1
		if (!newPuzzle) {
			//set an ID to the gridview for future references
			GridView gridView = new GridView(MainActivity.this);
			gridView.setId(1);
			gridView.setLayoutParams(new GridView.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT));
			gridView.setBackgroundColor(Color.WHITE);
			gridView.setNumColumns(4);
			gridView.setColumnWidth(GridView.AUTO_FIT);
			gridView.setVerticalSpacing(0);
			gridView.setHorizontalSpacing(0);
			gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH); gridView.setSelector(android.R.color.transparent);
			
			gridView.setAdapter(new Listview1Adapter(puzzle));
			((BaseAdapter)gridView.getAdapter()).notifyDataSetChanged();
			
			gridlayout.addView(gridView);
			
			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
					
					SketchwareUtil.showMessage(getApplicationContext(), "Clicked");
				}
			});
			
			gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override public boolean onItemLongClick(AdapterView gridView, View view, int position, long row) { 
					ClipData.Item item = new ClipData.Item((String) view.getTag()); 
					ClipData clipData = new ClipData((CharSequence) view.getTag(), 
					new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN }, item); 
					view.startDrag(clipData, new View.DragShadowBuilder(view), null, 0); 
					//view.setVisibility(View.INVISIBLE); 
					draggedIndex = position; 
					return true; 
				} 
			});
			
			newPuzzle = true;
		}
		_refresh();
	}
	
	
	public void _checkwin() {
		//check if the numbers in the array are in correct order
		pos = 0;
		wrong = 0;
		for(int _repeat37 = 0; _repeat37 < (int)(puzzled.size()); _repeat37++) {
			if (!(pos == puzzled.get((int)(pos)).doubleValue())) {
				wrong++;
			}
			pos++;
		}
		if (wrong == 0) {
			SketchwareUtil.showMessage(getApplicationContext(), "win");
		}
	}
	
	
	public void _cropper() {
	}
	/* private void performCrop(Uri picUri) { 
try { 
Intent cropIntent = new Intent("com.android.camera.action.CROP");
cropIntent.setDataAndType(picUri, "image/*");
cropIntent.putExtra("crop", true);
cropIntent.putExtra("aspectX", 1);
cropIntent.putExtra("aspectY", 1);
cropIntent.putExtra("outputX", 128);
cropIntent.putExtra("outputY", 128);
cropIntent.putExtra("return-data", true);
startActivityForResult(cropIntent, PIC_CROP); 
} catch (ActivityNotFoundException anfe) { 
final String errorMessage = "Whoops - your device doesn't support the crop action!"; Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT); toast.show(); }
} */
	{
	}
	
	public class Listview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.items, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView imageview1 = _view.findViewById(R.id.imageview1);
			
			if (newPuzzle) {
				rndm = puzzled.get((int)(_position)).doubleValue();
				imageview1.setImageBitmap(chunkedImages.get((int)(rndm)));
				_Dragger(linear1);
				imageview1.getLayoutParams().height = (int)size;
				imageview1.getLayoutParams().width = (int)size;
				imageview1.requestLayout();
			}
			
			return _view;
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}