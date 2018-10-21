package io.github.petrukha.arranger.test;

import io.github.petrukha.arranger.domain.Chord;
import io.github.petrukha.arranger.domain.Harmony;
import io.github.petrukha.arranger.domain.Note;
import io.github.petrukha.arranger.domain.PatternType;
import io.github.petrukha.arranger.domain.TimedChord;
import io.github.petrukha.arranger.service.ArrangementService;
import io.github.petrukha.arranger.service.PatternChecker;
import io.github.petrukha.arranger.service.PatternLoader;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ArrangementServiceTest {
    private final static String MAJOR_TRIAD = "MAJOR_TRIAD";

    @Mock
    private List<String> someList;

    @Mock
    private PatternChecker checkerMock;

    @Mock
    private PatternLoader loaderMock;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testArrange() throws InvalidMidiDataException {
        when(checkerMock.checkAvailablePatternTypes(anyString(), eq(MAJOR_TRIAD))).
                thenReturn(Arrays.asList(new PatternType(120, false),
                        new PatternType(0, true)));
        when(loaderMock.load(anyString(), eq(new Chord(Note.C, Note.C, MAJOR_TRIAD)),
             eq(new PatternType(120, false))))
            .thenReturn(Arrays.asList(
                        new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 93), 0)));
        when(loaderMock.load(anyString(), eq(new Chord(Note.G, Note.G, MAJOR_TRIAD)),
                argThat(patternType -> patternType.isCadence())))
                .thenReturn(Arrays.asList(
                        new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 70, 93), 0)));
        ArrangementService arrangementService = new ArrangementService(checkerMock, loaderMock);


        Harmony harmony = new Harmony(new TimedChord(new Chord(Note.C, Note.C, MAJOR_TRIAD), 0),
                                      new TimedChord(new Chord(Note.G, Note.G, MAJOR_TRIAD), 120));
        List<MidiEvent> arrangement = arrangementService.arrange("test", harmony);

        assertEquals(arrangement.size(), 2);
        arrangement.forEach(e -> assertTrue(e.getMessage() instanceof ShortMessage));
        assertEquals(arrangement.stream()
                .filter(midiEvent -> midiEvent.getTick() == 0 && ((ShortMessage)midiEvent.getMessage()).getData1() == 60)
                .count(),
                1);
        assertEquals(arrangement.stream()
                        .filter(midiEvent -> midiEvent.getTick() == 120 && ((ShortMessage)midiEvent.getMessage()).getData1() == 70)
                        .count(),
                1);
    }
}
