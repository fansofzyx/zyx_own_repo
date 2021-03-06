package cn.easyar.samples.helloarmultitargetmt;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wuwang on 2017/1/7
 */

public class ObjReader {


    public static List<Obj3D> readMultiObj(Context context,String file){
        boolean isAssets;
        ArrayList<Obj3D> data=new ArrayList<>();
        ArrayList<Float> oVs=new ArrayList<Float>();//原始顶点坐标列表
        ArrayList<Float> oVNs=new ArrayList<>();    //原始顶点法线列表
        ArrayList<Float> oVTs=new ArrayList<>();    //原始贴图坐标列表
        ArrayList<Float> oFVs=new ArrayList<>();     //面顶点
        ArrayList<Float> oFVNs=new ArrayList<>();
        ArrayList<Float> oFVTs=new ArrayList<>();
        HashMap<String,MtlInfo> mTls=null;
        HashMap<String,Obj3D> mObjs=new HashMap<>();
        Obj3D nowObj=null;
        MtlInfo nowMtl=null;
        try{
            String parent;
            InputStream inputStream;
            if (file.startsWith("assets/")){
                isAssets=true;
                String path=file.substring(7);
                parent=path.substring(0,path.lastIndexOf("/")+1);
                inputStream=context.getAssets().open(path);
                Log.e("obj",parent);
            }else{
                isAssets=false;
                parent=file.substring(0,file.lastIndexOf("/")+1);
                inputStream=new FileInputStream(file);
            }
            InputStreamReader isr=new InputStreamReader(inputStream);
            BufferedReader br=new BufferedReader(isr);
            String temps;
            while((temps=br.readLine())!=null){
                if("".equals(temps)){

                }else{
                    String[] tempsa=temps.split("[ ]+");

                    switch (tempsa[0].trim()){
                        case "mtllib":  //材质
                            InputStream stream;
                            Log.e("ObjReader","read_mtllib"+temps);
                            if (isAssets){
                                stream=context.getAssets().open(parent+tempsa[1]);

                            }else{
                                stream=new FileInputStream(parent+tempsa[1]);
                            }
                            mTls=readMtl(stream);
                            // Log.e("mTls",)
                            break;
                        case "usemtl":  //采用纹理
                            Log.e("ObjReader","read_usemtl"+temps);
                            if(mTls!=null){
                                nowMtl=mTls.get(tempsa[1]);
                            }
                            if(mObjs.containsKey(tempsa[1])){
                                nowObj=mObjs.get(tempsa[1]);
                            }else{
                                nowObj=new Obj3D();
                                nowObj.mtl=nowMtl;
                                mObjs.put(tempsa[1],nowObj);
                            }
                            break;
                        case "v":       //原始顶点
                            read(tempsa,oVs);
                            break;
                        case "vn":      //原始顶点法线
                            read(tempsa,oVNs);
                            break;
                        case "vt":
                            read(tempsa,oVTs);
                            break;
                        case "f":
                            ArrayList<Integer> vnList = new ArrayList<Integer>();
                            for (int i=1;i<tempsa.length;i++){
                                String[] fs=tempsa[i].split("/");
                                //Log.e("Obj","f"+tempsa[i]);
                                int index;
                                if(fs.length>0){
                                    //顶点索引
                                    index=Integer.parseInt(fs[0])-1;
                                    nowObj.addVert(oVs.get(index*3));
                                    nowObj.addVert(oVs.get(index*3+1));
                                    nowObj.addVert(oVs.get(index*3+2));
                                }
                                if(fs.length>1){
                                    //贴图
                                    index=Integer.parseInt(fs[1])-1;
                                    nowObj.addVertTexture(oVTs.get(index*2));
                                    nowObj.addVertTexture(oVTs.get(index*2+1));
                                }
                                if(fs.length>2){
                                    //法线索引
                                    Log.e("Buffer","vertBuffer");
                                    index=Integer.parseInt(fs[2])-1;
                                    nowObj.addVertNorl(oVNs.get(index*3));
                                    nowObj.addVertNorl(oVNs.get(index*3+1));
                                    nowObj.addVertNorl(oVNs.get(index*3+2));
                                }
                                else
                                {
                                    int ind=Integer.parseInt(fs[0])-1;
                                    vnList.add(ind);
                                }
                            }
                            if(vnList.size()>0)
                            {
                                float ab[] =new float[3];
                                float bc[] = new float[3];
                                float vertexs[] =  new float[9];
                                float norl[] = new float[3];
                                for(int i=0;i<vnList.size();i++)
                                {
                                    for(int j=0;j<3;j++)
                                    {
                                        vertexs[i*3+j] = oVs.get(vnList.get(i)*3+j);
                                    }
                                }
                                ab[0] = vertexs[3]-vertexs[0];
                                ab[1] = vertexs[4]-vertexs[1];
                                ab[2] = vertexs[5]-vertexs[2];
                                bc[0] = vertexs[6]-vertexs[3];
                                bc[1] = vertexs[7]-vertexs[4];
                                bc[2] = vertexs[8]-vertexs[5];
                                norl[0]=ab[1]*bc[2]-ab[2]*bc[1];
                                norl[1]=ab[2]*bc[0]-ab[0]*bc[2];
                                norl[2]=ab[0]*bc[1]-ab[1]*bc[0];
                                for(int i=0;i<3;i++)
                                {
                                    nowObj.addVertNorl(norl[0]);
                                    nowObj.addVertNorl(norl[1]);
                                    nowObj.addVertNorl(norl[2]);
                                    oVNs.add(norl[0]);
                                    oVNs.add(norl[1]);
                                    oVNs.add(norl[2]);
                                }
                            }
                            break;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        for (Map.Entry<String, Obj3D> stringObj3DEntry : mObjs.entrySet()) {
            Obj3D obj = stringObj3DEntry.getValue();
            obj.dataLock();
            data.add(obj);
            // obj.dataLock();
        }
        Log.e("Obj","dataSize:"+data.size());
        return data;
    }

    public static HashMap<String,MtlInfo> readMtl(InputStream stream){
        HashMap<String,MtlInfo> map=new HashMap<>();
        try{
            InputStreamReader isr=new InputStreamReader(stream);
            BufferedReader br=new BufferedReader(isr);
            String temps;
            MtlInfo mtlInfo=new MtlInfo();
            while((temps=br.readLine())!=null)
            {
                String[] tempsa=temps.split("[ ]+");
                switch (tempsa[0].trim()){
                    case "newmtl":  //材质
                        mtlInfo=new MtlInfo();
                        mtlInfo.newmtl=tempsa[1];
                        map.put(tempsa[1],mtlInfo);
                        Log.e("readMlt","my:"+tempsa[0]);
                        break;
                    case "illum":     //光照模型
                        mtlInfo.illum=Integer.parseInt(tempsa[1]);
                        Log.e("readMlt","my:"+tempsa[0]);
                        break;
                    case "Kd":
                        read(tempsa,mtlInfo.Kd);
                        Log.e("readMlt","my:"+tempsa[0]);
                        break;
                    case "Ka":
                        read(tempsa,mtlInfo.Ka);
                        Log.e("readMlt","my:"+tempsa[0]);
                        break;
                    case "Ke":
                        read(tempsa,mtlInfo.Ke);
                        Log.e("readMlt","my:"+tempsa[0]);
                        break;
                    case "Ks":
                        read(tempsa,mtlInfo.Ks);
                        Log.e("readMlt","my:"+tempsa[0]);
                        break;
                    case "Ns":
                        mtlInfo.Ns=Float.parseFloat(tempsa[1]);
                    case "map_Kd":
                        mtlInfo.map_Kd=tempsa[1];
                        Log.e("readMlt","my:"+tempsa[0]);
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    private static void read(String[] value,ArrayList<Float> list){
        for (int i=1;i<value.length;i++){
            list.add(Float.parseFloat(value[i]));
        }
    }

    private static void read(String[] value,float[] fv){
        for (int i=1;i<value.length&&i<fv.length+1;i++){
            fv[i-1]=Float.parseFloat(value[i]);
        }
    }

}
