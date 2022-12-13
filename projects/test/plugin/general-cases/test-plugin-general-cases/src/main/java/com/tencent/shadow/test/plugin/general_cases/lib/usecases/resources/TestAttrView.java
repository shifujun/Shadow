package com.tencent.shadow.test.plugin.general_cases.lib.usecases.resources;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.R;

public class TestAttrView extends TextView {

   public final String refName;

   public TestAttrView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TestDeclareStyleable);
      int resourceId = typedArray.getResourceId(R.styleable.TestDeclareStyleable_testRefAttr, -1);
      refName = getResources().getResourceName(resourceId);
   }
}
