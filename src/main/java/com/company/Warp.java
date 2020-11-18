package com.company;

import java.awt.*;

import static java.lang.Math.*;

public class Warp {
    //全部为x,y形式
    public static void warp(float[] center, float[] orient,float height, float width, ImageOperator operator){
        float down,top,left,right;
        float radius2,radius;
        float direct0,direct1;
        //数据检查
        {
            if(center.length != 2) return;
            if(orient.length != 2) return;
        }

        direct0 = orient[0] - center[0];
        direct1 = orient[1] - center[1];
        radius2 = direct0 * direct0 + direct1 * direct1;
        radius = (float) sqrt((double) radius2);


        //计算radius

        top = Math.max(0, center[1]-radius);
        down= Math.min(height - 1, center[1] + radius);
        left= Math.max(0,center[0]-radius);
        right = Math.min(width-1, center[0]+radius);

        for(int i = (int) top; i <= down; i++){//y
            for (int j = (int) left; j<= right; j++){//x
                float len2 = (j-center[0])*(j-center[0]) + (i-center[1])*(i-center[1]);
                if(len2 <= radius2) {//圈内
                    float e = 1 - (radius2/(2*radius2 - len2));
                    e = e*e;
                    float source_0 = j - e * direct0;
                    float source_1 = i - e * direct1;

                    if( source_0>=0 && source_0 <= width -1 && source_1 >=0 && source_1 <= height -1){
                        operator.setPixelColor(j,i,(int)source_0,(int) source_1);
                    }


                }
            }
        }

    }


}
