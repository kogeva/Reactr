package com.example.reactr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import org.json.JSONObject;

public class PageFragment extends Fragment {
  
  static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
  final String mimeType = "text/html";
  final String encoding = "UTF-8";
  String megaString;
  int pageNumber;
  int backColor;
  
  JSONObject json;
  
  static PageFragment newInstance(int page) {
    PageFragment pageFragment = new PageFragment();
    Bundle arguments = new Bundle();
    arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
    pageFragment.setArguments(arguments);
    return pageFragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment, null);
 
    ImageView tvPage = (ImageView) view.findViewById(R.id.tvPage);
	

		if(pageNumber==0)	
		{
			tvPage.setImageResource(R.drawable.slide_first);
			
		}
	     if(pageNumber==1)	
	 	{
	    	 tvPage.setImageResource(R.drawable.slide2);
	    
	 	}
	     if(pageNumber==2)	
	 	{
	    	 tvPage.setImageResource(R.drawable.slide3);
	 	}
	     
	     if(pageNumber==3)	
	 	{
	    	 tvPage.setImageResource(R.drawable.slide4); 
	 	} 
	     if(pageNumber==4)	
		 	{
		    	 tvPage.setImageResource(R.drawable.slide5); 
		 	}
      if(pageNumber==5)
      {
          tvPage.setImageResource(R.drawable.slide6);
      }
      if(pageNumber==6)
      {
          tvPage.setImageResource(R.drawable.slide7);
      }
      if(pageNumber==7)
      {
          tvPage.setImageResource(R.drawable.slide8);
      }



      return view;
  }
  
  
  
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	  
	  }
}