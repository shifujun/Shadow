package test.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class UseStartActivityFragment {

    Activity test(TestFragment fragment) {
        fragment.startActivity(new Intent());
        fragment.startActivity(new Intent(), new Bundle());
        return fragment.getActivity();
    }
}
