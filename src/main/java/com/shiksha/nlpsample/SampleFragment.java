/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shiksha.nlpsample;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.shiksha.nlp.loc.parser.CustomLocation;
import org.shiksha.nlp.loc.parser.CustomLocationListener;
import org.shiksha.nlp.loc.posloc.POSLocationBuilder;


/** The UI fragment that hosts a logging view.
 * @author Purushotham*/
//@Keep
public class SampleFragment extends Fragment
        implements CustomLocationListener
{

    private static final String CLIENT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTaGlraGF0ZWNoIiwiaWF0IjoxNzE1MzI5ODIxLCJleHAiOjE3MTU0MTYyMjF9.h6sYRr0eMJvOASCV9zgvaJfBllXsKhDTOvJQPAAst6E";
    private TextView mLogView;
    private ScrollView mScrollView;
    private Button mStartLog;
    private Button autoScollBtn;
    private Button mStopLog;
    private static boolean autoScroll = true;

    private static final int MAX_LENGTH = 42000;
    private static final int LOWER_THRESHOLD = (int) (MAX_LENGTH * 0.5);
    private POSLocationBuilder poslocationBuilder;

    @Override
    public void onResume() {
        super.onResume();
        startLocationService();
    }

    private void startLocationService() {
        if(poslocationBuilder ==null){
            poslocationBuilder = new POSLocationBuilder(getContext(),CLIENT_TOKEN, this);
        }
        //interval in min's min 2 minutes
        poslocationBuilder.setIntervalTimeInMins(2);
        poslocationBuilder.startLocationCapture();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationCapture();
    }

    private void stopLocationCapture() {
        if(poslocationBuilder !=null){
            poslocationBuilder.stopLocationCapture();
            poslocationBuilder.getLocation(requireContext());
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View newView = inflater.inflate(R.layout.fragment_log, container, false /* attachToRoot */);
        mLogView = (TextView) newView.findViewById(R.id.log_view);
        mScrollView = (ScrollView) newView.findViewById(R.id.log_scroll);

        Button start = (Button) newView.findViewById(R.id.start_log);
        Button end = (Button) newView.findViewById(R.id.end_log);
        Button clear = (Button) newView.findViewById(R.id.clear_log);

        start.setOnClickListener(
                view -> mScrollView.fullScroll(View.FOCUS_UP));

        end.setOnClickListener(
                view -> mScrollView.fullScroll(View.FOCUS_DOWN));

        clear.setOnClickListener(
                view -> mLogView.setText(""));

        autoScollBtn = (Button) newView.findViewById(R.id.autoScoll);
        mStartLog = (Button) newView.findViewById(R.id.start_logs);
        mStopLog = (Button) newView.findViewById(R.id.stop);
        enableOptions( /* start */);

        mStartLog.setOnClickListener(
                view -> {
//                    enableOptions(false /* start */);
                    Toast.makeText(getContext(), R.string.start_message, Toast.LENGTH_LONG).show();
                    startLocationService();
                });

        /*restart.setOnClickListener(
                view -> stopAndSend()); */
        mStopLog.setOnClickListener(
                view -> {
                    stopLocationCapture();
                });

        autoScollBtn.setOnClickListener(v->{
            autoScroll=!autoScroll;
            printLog("Auto Scroll","Status: "+autoScroll, Color.WHITE);
        });
        mLogView.setOnTouchListener((v, event) -> {
            autoScroll=false;
            printLog("Auto Scroll","Status: "+autoScroll, Color.WHITE);
            return false;
        });
        return newView;
    }




    private void enableOptions() {
        autoScollBtn.setEnabled(true);
        mStartLog.setEnabled(true);
    }




    private void printLog(String tag, String text, int color) {
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(tag).append(" | ").append(text).append("\n");
      /*  builder.setSpan(
                new ForegroundColorSpan(color),
                0 *//* start *//*,
                builder.length(),
                SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);*/

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(
                () -> {
                    if(mLogView!=null){
                        mLogView.append(builder);
                        Editable editable = mLogView.getEditableText();
                        int length = editable.length();
                        if (length > MAX_LENGTH) {
                            editable.delete(0, length - LOWER_THRESHOLD);
                        }
                        if (autoScroll) {
                            mScrollView.post(
                                    () -> mScrollView.fullScroll(View.FOCUS_DOWN));
                        }
                    }
                });
    }


    @Override
    public void onLocationChanged(CustomLocation location) {
        printLog("loc","provider: "+location.getProvider()
                +"latlng: "+location.getLatitude() +","+location.getLongitude()
                +" accuracy: "+location.getAccuracy() +" time: "+location.getTime(),Color.WHITE);
    }
}
