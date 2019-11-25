package com.student.ramirez.quizplusplus;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabHome extends Fragment {


    public TabHome() {
        // Required empty public constructor
    }


    private Button btnExit;
    private ImageView ivAppLogo;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_home, container, false);

        ivAppLogo = (ImageView) view.findViewById(R.id.ivAppLogo);
        RunFloatAnimation();



        return view;
    }
    public void RunFloatAnimation(){
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.float_animation);
        animation.reset();
        ivAppLogo.clearAnimation();
        ivAppLogo.startAnimation(animation);

    }

}
