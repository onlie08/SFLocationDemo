package com.sfmap.api.maps.utils;

import com.sfmap.api.maps.MapUtils;
import com.sfmap.api.maps.model.LatLng;

import java.util.List;

public class SpatialRelationUtil {

    public static LatLng calShortestDistancePoint(java.util.List<LatLng> points, LatLng point){
        int pos = getNearestPoint(points,point);
        if(pos == -1){
            return null;
        }
        if(pos == 0){
            return points.get(0);
        }
        if(pos == points.size()-1){
            return points.get(points.size()-1);
        }
        LatLng beforeLatlng = points.get(pos-1);
        LatLng curLatlng = points.get(pos);
        LatLng nextLatlng = points.get(pos+1);

        if(curLatlng.equals(nextLatlng) && points.size()>pos+1){
            nextLatlng = points.get(pos+2);
        }

        LatLng before = getProjectivePoint(beforeLatlng,curLatlng,point);
        LatLng next = getProjectivePoint(curLatlng,nextLatlng,point);

        int beforeAngle = -1;
        int nextAngle = -1;

        if(before !=null){
            beforeAngle = getIn_angle(curLatlng.latitude,curLatlng.longitude,beforeLatlng.latitude,beforeLatlng.longitude,before.latitude,before.longitude);
        }
        if(next !=null){
            nextAngle = getIn_angle(curLatlng.latitude,curLatlng.longitude,nextLatlng.latitude,nextLatlng.longitude,next.latitude,next.longitude);
        }
        if(beforeAngle == 0){
            return before;
        }
        if(nextAngle == 0){
            return next;
        }
        return curLatlng;
    }

    public static LatLng calShortestDistancePoints(java.util.List<LatLng> points, LatLng point){
        double distance = 0;
        LatLng result = null;
        for(int i=0;i<points.size()-1;i++){
            LatLng curLatlng = points.get(i);
            LatLng nextLatlng = points.get(i+1);
            if(curLatlng.equals(nextLatlng)){
                continue;
            }
            LatLng next = getProjectivePoint(curLatlng,nextLatlng,point);
            int nextAngle = getIn_angle(curLatlng.latitude,curLatlng.longitude,nextLatlng.latitude,nextLatlng.longitude,next.latitude,next.longitude);
            if(nextAngle == 180){
                float thisLength = MapUtils.calculateLineDistance(next,point);
                if(i == 0 ){
                    distance = thisLength;
                    continue;
                }
                if(thisLength<distance){
                    distance = thisLength;
                    result = next;
                }
            }
        }
        return result;
    }

    /**
     * 返回一个距离指定坐标点最近的一个坐标点
     *
     * @param list  待比较的坐标点集合
     * @param point 指定点
     * @return
     */
    public static int getNearestPoint(List<LatLng> list, LatLng point) {
        float minLength = 0;
        int i1 = 0;
        for(int i=0;i<list.size();i++){
            float thisLength = MapUtils.calculateLineDistance(list.get(i),point);
            if (minLength == 0) {
                minLength = thisLength;
            }
            if (thisLength < minLength) {
                minLength = thisLength;
                i1 = i;
            }
        }
        return i1;
    }



    private  static int getIn_angle(double x1, double x2, double y1, double y2, double z1, double z2) {
        //向量的点乘
        double t =(y1-x1)*(z1-x1)+(y2-x2)*(z2-x2);

        //为了精确直接使用而不使用中间变量
        //包含了步骤：A=向量的点乘/向量的模相乘
        //          B=arccos(A)，用反余弦求出弧度
        //          result=180*B/π 弧度转角度制
        int result =(int)(180*Math.acos(
                         t/Math.sqrt
                         ((Math.abs((y1-x1)*(y1-x1))+Math.abs((y2-x2)*(y2-x2)))
                *(Math.abs((z1-x1)*(z1-x1))+Math.abs((z2-x2)*(z2-x2)))
                         ))
                         /Math.PI);
                 return result;
             }


    /**
     * 求直线外一点到直线上的投影点
     *
     * @param pLine    线上一点
     * @param pLine2   线上一点
     * @param pOut     线外一点
     * @return  result 投影点
     */
    public static LatLng getProjectivePoint(LatLng pLine, LatLng pLine2, LatLng pOut) {
        double x1 = pLine.longitude;
        double y1 = pLine.latitude;

        double x2 = pLine2.longitude;
        double y2 = pLine2.latitude;

        double x0 = pOut.longitude;
        double y0 = pOut.latitude;

        double A = y2 - y1;
        double B = x1 - x2;
        double C = x2*y1 - x1*y2;

        if(A == 0 && B == 0){
            return null;
        }
        double x = (B*B*x0 - A*B*y0 - A*C) / (A*A + B*B);
        double y = (-A*B*x0 + A*A*y0 - B*C) / (A*A + B*B);

        LatLng result = new LatLng(y,x);

        return result;
    }
}
