package cn.easyar.samples.helloarmultitargetmt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Obj3DRendererNew {
    private Context context;
    private List<String> mTextureNameList;
    private World mWorld;
    private Light mLight;
    private com.threed.jpct.Object3D  mObj3D;
    private FrameBuffer fb;
    private int w,h;
    Obj3DRendererNew(Context context)
    {
        this.context =  context;

    }
    public  void setFb(int w,int h)
    {
        if(fb!=null)
            fb=null;
        fb = new FrameBuffer(w, h);
    }
   /* public String readMtlName(String objPath)
    {
        String mtlName = null;
        try {
            File objFile = new File(objPath);
            InputStream inStream = new FileInputStream(objFile);
            if (inStream != null)
            {
                InputStreamReader inStreamReader = new InputStreamReader(inStream);
                BufferedReader bufferedReader = new BufferedReader(inStreamReader);
                String line;
                while((line=bufferedReader.readLine())!=null)
                {
                    int ind = line.indexOf("mtllib");
                    if(ind>=0)
                    {
                        mtlName = line.substring(ind+7);
                        break;
                    }
                    else if(line.startsWith("v"))
                    {
                        break;
                    }
                }
                bufferedReader.close();
                inStreamReader.close();
                inStream.close();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mtlName;
    }*/
//    public int parseTextureNames(String mtlPath)
//    {
//        try {
//            File mtlFile = new File(mtlPath);
//            String texureName = null;
//            InputStream inStream = new FileInputStream(mtlFile);
//            if(inStream != null)
//            {
//                InputStreamReader inputReader = new InputStreamReader(inStream);
//                BufferedReader bufferedReader = new BufferedReader(inputReader);
//                String line;
//                while((line=bufferedReader.readLine())!=null)
//                {
//                    int ind = line.indexOf("map_Kd");
//                    if(ind>=0)
//                    {
//                        texureName = line.substring(ind+7);
//                        if(!mTextureNameList.contains(texureName))
//                        {
//                            mTextureNameList.add(texureName);
//                        }
//                    }
//                    else if(line.indexOf("map_Ka")>=0)
//                    {
//                        texureName = line.substring(ind+7);
//                        if(!mTextureNameList.contains(texureName))
//                        {
//                            mTextureNameList.add(texureName);
//                        }
//                    }
//                }
//                bufferedReader.close();
//                inputReader.close();
//                inStream.close();
//            }
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return mTextureNameList.size();
//    }
//    public Bitmap scaleBitmap(Bitmap bitmap){
//        int w= bitmap.getWidth();
//        int h= bitmap.getHeight();
//        int destW=1024;
//        int destH= h*destW/w;
//        Bitmap newbm = Bitmap.createScaledBitmap(bitmap, destW, destH, true);
//        return newbm;
//    }
//    public void loadTextures(){
//        List<String> tList = mTextureNameList;
//        TextureManager tm = TextureManager.getInstance();
//        for(int i=0;i<tList.size();i++)
//        {
//            String name = tList.get(i);
//            try {
//                Bitmap bmp = BitmapFactory.decodeStream(context.getAssets().open(name));
//                Bitmap inputBmp = bmp;
//                int w=bmp.getWidth();
//                if((w&(w-1))!=0){
//                    inputBmp=scaleBitmap(bmp);
//                }
//                com.threed.jpct.Texture texture = new com.threed.jpct.Texture(inputBmp);
//                tm.addTexture(name,texture);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
    public void init()
    {
        mWorld = new World();
        //添加环境光
        mWorld.setAmbientLight(20,20,20);
        mLight = new Light(mWorld);

        try {
            com.threed.jpct.Texture t1 = new com.threed.jpct.Texture(context.getAssets().open("01Image1.png"));
            TextureManager.getInstance().addTexture("01Image1.png", t1);
            mObj3D = Object3D.mergeAll(Loader.loadOBJ(context.getAssets().open("01.obj"), context.getAssets().open("01.mtl"), 2));
            mObj3D.setCulling(false);
            mWorld.addObjects(mObj3D);
            mObj3D.strip();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void render()
    {
        mWorld.renderScene(fb);
        mWorld.draw(fb);
        fb.display();
    }
}
