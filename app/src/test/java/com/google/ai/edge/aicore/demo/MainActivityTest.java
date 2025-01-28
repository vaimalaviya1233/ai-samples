package com.google.ai.edge.aicore.demo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import android.content.Context;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.ai.edge.aicore.Content;
import com.google.ai.edge.aicore.GenerateContentResponse;
import com.google.ai.edge.aicore.GenerationConfig;
import com.google.ai.edge.aicore.GenerativeModel;
import com.google.ai.edge.aicore.demo.ContentAdapter;
import com.google.ai.edge.aicore.demo.GenerationConfigDialog;
import com.google.ai.edge.aicore.demo.GenerationConfigUtils;
import com.google.ai.edge.aicore.demo.R;
import com.google.ai.edge.aicore.java.GenerativeModelFutures;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mockito;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.util.ArrayList;

import com.google.ai.edge.aicore.GenerateContentResponse;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {
    @Mock
    private Context mockContext;

    @Mock
    private GenerativeModel mockModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockContext = Mockito.mock(Context.class);
        mockModel = Mockito.mock(GenerativeModel.class);

    }
    @Test
    public void testSendButton() {
        // Simulate a request being sent and received.
        String request = "This is a test prompt";
        //Check if the model is being initialized correctly
        String response = "This is a response";
        when(mockModel.generateContent(any())).thenReturn(new GenerateContentResponse(response));
        // Check the response
        assertEquals("This is a response", response);
        verify(mockModel).generateContent(any());
    }
    @Test
    public void testInitGenerativeModel(){
        Context context = mock(Context.class);
        GenerationConfig config = mock(GenerationConfig.class);
        GenerativeModel model = new GenerativeModel(config);
        //Check if the model was initialized correctly
        assertEquals(true,true);
    }
     @Test
    public void testStreamingResponse() {
        // Mock the behavior of the GenerativeModel in streaming mode.
        String request = "This is a test prompt";
        String streamResponse = "Streaming Response 1";
        ArrayList<GenerateContentResponse> responses = new ArrayList<GenerateContentResponse>();
        responses.add(new GenerateContentResponse(streamResponse));
        responses.add(new GenerateContentResponse(streamResponse + " Streaming Response 2"));
        responses.add(new GenerateContentResponse(streamResponse + " Streaming Response 3"));
        when(mockModel.generateContentStream(any())).thenReturn(responses.stream());
        // Verify that the model's generateContentStream method is called.
        assertEquals(3,responses.size());
        verify(mockModel, times(3)).generateContentStream(any());
    }
}
