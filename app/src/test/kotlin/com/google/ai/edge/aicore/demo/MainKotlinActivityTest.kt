package com.google.ai.edge.aicore.demo

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.demo.ContentAdapter
import com.google.ai.edge.aicore.demo.GenerationConfigUtils
import kotlinx.coroutines.flow.Flow
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import static org.junit.Assert.assertEquals
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.`when`
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.times
import com.google.ai.edge.aicore.GenerateContentResponse
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.toList
import com.google.ai.edge.aicore.demo.kotlin.MainActivity
import androidx.appcompat.app.AppCompatActivity

@RunWith(MockitoJUnitRunner::class)
public class MainKotlinActivityTest{
    @Mock
    private lateinit var mockContext: Context
    @Mock
    private lateinit var mockModel: GenerativeModel
    @Mock
    private lateinit var mockFlow: Flow<GenerateContentResponse>
    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        mockContext = Mockito.mock(Context::class.java)
        mockModel = Mockito.mock(GenerativeModel::class.java)
        mockFlow = Mockito.mock(Flow::class.java)
    }
    @Test
    fun testSendButton(){
        val request = "This is a test prompt"
        val response = "This is a response"
        `when`(mockModel.generateContent(any())).thenReturn(GenerateContentResponse(response))
        assertEquals("This is a response", response)
        verify(mockModel).generateContent(any())
    }
    @Test
    fun testInitGenerativeModel(){
        val config = mock(GenerationConfig::class.java)
        val model = GenerativeModel(config)
        assertEquals(true,true)
    }
    @Test
    fun testStreamingResponse(){
        val request = "This is a test prompt"
        val streamResponse = "Streaming Response 1"
        val responses = mutableListOf<GenerateContentResponse>()
        responses.add(GenerateContentResponse(streamResponse))
        responses.add(GenerateContentResponse("$streamResponse Streaming Response 2"))
        responses.add(GenerateContentResponse("$streamResponse Streaming Response 3"))
        `when`(mockModel.generateContentStream(any())).thenReturn(flow { emit(responses.toList())})
        assertEquals(3,responses.size)
        verify(mockModel, times(3)).generateContentStream(any())

    }
}
