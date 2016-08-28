package com.aledgroup.jakline;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

    private UpdateMapAfterUserInterection updateMapAfterUserInterection;
    private UpdateMapMoveUserInterection updateMapMoveUserInterection;
    private UpdateMapBeforeUserInterection updateMapBeforeUserInterection;

    public TouchableWrapper(Context context) {
        super(context);
        // Force the host activity to implement the UpdateMapAfterUserInterection Interface
        try {
            updateMapAfterUserInterection = (MainActivity) context;
            updateMapMoveUserInterection = (MainActivity) context;
            updateMapBeforeUserInterection = (MainActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement UpdateMapAfterUserInterection");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateMapBeforeUserInterection.onUpdateMapBeforeUserInterection();
                break;
            case MotionEvent.ACTION_MOVE:
                updateMapMoveUserInterection.onUpdateMapMoveUserInterection();
                break;
            case MotionEvent.ACTION_UP:
                updateMapAfterUserInterection.onUpdateMapAfterUserInterection();
                break;
            case MotionEvent.ACTION_CANCEL:
                updateMapAfterUserInterection.onUpdateMapAfterUserInterection();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // Map Activity must implement this interface
    public interface UpdateMapAfterUserInterection {
        void onUpdateMapAfterUserInterection();
    }

    // Map Activity must implement this interface
    public interface UpdateMapMoveUserInterection {
        void onUpdateMapMoveUserInterection();
    }

    // Map Activity must implement this interface
    public interface UpdateMapBeforeUserInterection {
        void onUpdateMapBeforeUserInterection();
    }
}
