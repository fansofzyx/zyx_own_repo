package cn.easyar.samples.helloarmultitargetmt;

import android.content.Context;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

import cn.easyar.Matrix44F;
import cn.easyar.Vec2F;

public class Obj3DRenderer {
    private List<ObjFilter2> filters;
    List<Obj3D> model;
    Context context;


    public void init(Context context)
    {
        this.context = context;
        model=ObjReader.readMultiObj(context,"assets/01.obj");
        filters=new ArrayList<>();
        for (int i=0;i<model.size();i++){
            ObjFilter2 f=new ObjFilter2(context.getResources());
            f.setObj3D(model.get(i));
            filters.add(f);
        }
        for (ObjFilter2 f:filters){
            f.create();
        }

    }
    public void render(Matrix44F projectionMatrix, Matrix44F cameraview, Vec2F size)
    {
        for (ObjFilter2 f:filters){
            Matrix.rotateM(f.getMatrix(),0,0.3f,0,1,0);
            f.draw(projectionMatrix,cameraview);
        }
    }
}
