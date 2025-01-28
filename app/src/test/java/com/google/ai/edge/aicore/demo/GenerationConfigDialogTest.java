package com.google.ai.edge.aicore.demo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import com.google.ai.edge.aicore.demo.GenerationConfigDialog;
import com.google.ai.edge.aicore.demo.GenerationConfigDialog.OnConfigUpdateListener;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mockito;
import android.view.LayoutInflater;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class GenerationConfigDialogTest {
     @Mock
    private Activity mockActivity;
    @Mock
    private OnConfigUpdateListener mockListener;
    @Mock
    private DialogInterface dialogInterface;
    private GenerationConfigDialog dialog;
    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        mockActivity = MockMockito.mock(Activity.class);
        mockListener= MockMockito.mock(OnConfigUpdateListener.class);
        dialogInterface = Mockito.mock(DialogInterface.class);
        dialog = Mockito.spy(GenerationConfigDialog.class);
    }
    @Test
    public void testOnConfigUpdated(){
        //check if the config is being updated
        dialog.onAttach(mockContext);
        dialog.setListener(mockListener);
        dialog.show(mockFragmentManager,null);
        dialog.onConfigUpdated();
        verify(mockListener).onConfigUpdated();
        assertEquals(true,true);

    }
}
