package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.model.TemperatureData;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;



/**
 * 
 * @date 2014-6-24
 * @author xieb
 * 
 */
public class PathView extends View {

	Paint  paint = new Paint();
	public PathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		    paint.setAntiAlias(true);                       //设置画笔为无锯齿  
		    paint.setColor(Color.BLACK);                    //设置画笔颜色  
		    canvas.drawColor(Color.WHITE);                  //白色背景  
		    //paint.setStrokeWidth((float) 3.0);              //线宽  
		    paint.setStyle(Style.FILL);  
		    canvas.save(); 
		    canvas.translate(100, 50);
		    Path path = new Path();                     //Path对象  
		    path.moveTo(50, 100);                           //起始点  
		    path.lineTo(50, 300);                           //连线到下一点  
		    path.lineTo(100, 500);                      //连线到下一点  
		    path.lineTo(400, 500);                      //连线到下一点  
		    path.lineTo(300, 300);                      //连线到下一点  
		    path.lineTo(450, 50);                           //连线到下一点  
		    path.lineTo(200, 200);    
		    //path.close();
		    canvas.drawPath(path, paint);     
		    canvas.restore();
	}
}


