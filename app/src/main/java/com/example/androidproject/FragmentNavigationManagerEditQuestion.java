package com.example.androidproject;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentNavigationManagerEditQuestion implements NavigationManagerEditQuestion{
    private static FragmentNavigationManagerEditQuestion mInstance;
    private FragmentManager fragmentManager;

    private EditQuestionActivity activity;

    public static FragmentNavigationManagerEditQuestion getmInstance(EditQuestionActivity activity)
    {
        if (mInstance==null)
            mInstance = new FragmentNavigationManagerEditQuestion();
        mInstance.Config(activity);
        return mInstance;
    }

    private void Config(EditQuestionActivity activity) {
        activity = activity;
        fragmentManager = activity.getSupportFragmentManager();
    }

    @Override
    public void ShowFragment(String setID, String quesID,int position) {
        ShowFragment(Fragment_Edit_Question.newInstance(setID,quesID,position),false);
    }

    private void ShowFragment(Fragment_Edit_Question fragment, boolean b) {
        FragmentManager fm = fragmentManager;
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.fragment_container_edit_question,fragment);
        ft.addToBackStack(null);
        if (b || !BuildConfig.DEBUG)
            ft.commitAllowingStateLoss();
        else
            ft.commit();
        fm.executePendingTransactions();
    }
}
