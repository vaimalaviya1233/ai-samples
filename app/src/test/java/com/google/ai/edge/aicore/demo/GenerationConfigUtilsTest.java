package com.google.ai.edge.aicore.demo;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenerationConfigUtilsTest {

    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockSharedPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockContext = Mockito.mock(Context.class);
        mockSharedPreferences = Mockito.mock(SharedPreferences.class);
    }

    @Test
    public void testGetTemperature() {
        // Test retrieving temperature from SharedPreferences
        float expectedTemperature = 0.2f;
        when(mockSharedPreferences.getFloat(Mockito.anyString(), Mockito.anyFloat())).thenReturn(expectedTemperature);
        float actualTemperature = GenerationConfigUtils.getTemperature(mockContext);
        assertEquals(expectedTemperature, actualTemperature, 0.001f);
    }

    @Test
    public void testSetTemperature() {
        // Test setting temperature in SharedPreferences
        float temperatureToSet = 0.5f;
        GenerationConfigUtils.setTemperature(mockContext, temperatureToSet);
        float retrievedTemperature = mockSharedPreferences.getFloat(Mockito.anyString(), Mockito.anyFloat());
        assertEquals(temperatureToSet, retrievedTemperature, 0.001f);
    }
      @Test
    public void testGetTopK() {
        // Test retrieving topK from SharedPreferences
        int expectedTopK = 16;
        when(mockSharedPreferences.getInt(Mockito.anyString(), Mockito.anyInt())).thenReturn(expectedTopK);
        int actualTopK = GenerationConfigUtils.getTopK(mockContext);
        assertEquals(expectedTopK, actualTopK);
    }

    @Test
    public void testSetTopK() {
        // Test setting topK in SharedPreferences
        int topKToSet = 32;
        GenerationConfigUtils.setTopK(mockContext, topKToSet);
        int retrievedTopK = mockSharedPreferences.getInt(Mockito.anyString(), Mockito.anyInt());
        assertEquals(topKToSet, retrievedTopK);
    }
      @Test
    public void testGetMaxOutputTokens() {
        // Test retrieving maxOutputTokens from SharedPreferences
        int expectedMaxOutputTokens = 256;
        when(mockSharedPreferences.getInt(Mockito.anyString(), Mockito.anyInt())).thenReturn(expectedMaxOutputTokens);
        int actualMaxOutputTokens = GenerationConfigUtils.getMaxOutputTokens(mockContext);
        assertEquals(expectedMaxOutputTokens, actualMaxOutputTokens);
    }

    @Test
    public void testSetMaxOutputTokens() {
        // Test setting maxOutputTokens in SharedPreferences
        int maxOutputTokensToSet = 512;
        GenerationConfigUtils.setMaxOutputTokens(mockContext, maxOutputTokensToSet);
        int retrievedMaxOutputTokens = mockSharedPreferences.getInt(Mockito.anyString(), Mockito.anyInt());
        assertEquals(maxOutputTokensToSet, retrievedMaxOutputTokens);
    }
    @Test
    public void testDefaultTemperature(){
        //check default temperature
        float defaultTemperature = 0.2f;
        float temperature = GenerationConfigUtils.getTemperature(mockContext);
        assertEquals(defaultTemperature,temperature, 0.001f);

    }
     @Test
    public void testDefaultTopK(){
        //check default TopK
        int defaultTopK = 16;
        int topK = GenerationConfigUtils.getTopK(mockContext);
        assertEquals(defaultTopK,topK);
    }
     @Test
    public void testDefaultMaxOutputTokens(){
        //check default MaxOutputTokens
        int defaultMaxOutputTokens = 256;
        int maxOutputTokens = GenerationConfigUtils.getMaxOutputTokens(mockContext);
        assertEquals(defaultMaxOutputTokens,maxOutputTokens);
    }
}
