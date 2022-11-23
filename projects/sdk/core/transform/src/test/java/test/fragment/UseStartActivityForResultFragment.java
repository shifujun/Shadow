package test.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class UseStartActivityForResultFragment {

    Activity test(TestFragment fragment) {
        fragment.startActivityForResult(new Intent(), 1);
        fragment.startActivityForResult(new Intent(), 1, new Bundle());
        return fragment.getActivity();
    }
}
