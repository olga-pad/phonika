package com.phonika.domain;

import com.phonika.learning.domain.Grapheme;
import com.phonika.learning.domain.Language;
import com.phonika.learning.domain.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class WordGraphemeSequenceTest {

    @Test void russianWordPreservesOrderedSingleLetterGraphemes() {
        Grapheme k = grapheme("к", Language.RU);
        Grapheme o = grapheme("о", Language.RU);
        Grapheme t = grapheme("т", Language.RU);
        Word kot = word("кот", Language.RU, k, o, t);

        assertEquals(Language.RU, kot.language());
        assertEquals(3, kot.graphemes().size());
        assertEquals(List.of("к", "о", "т"), representations(kot));
    }

    @Test void englishTrigraphRemainsOneGraphemeInsideWord() {
        Grapheme n = grapheme("n", Language.EN);
        Grapheme igh = grapheme("igh", Language.EN);
        Grapheme t = grapheme("t", Language.EN);
        Word night = word("night", Language.EN, n, igh, t);

        assertEquals(3, night.graphemes().size());
        assertSame(igh, night.graphemes().get(1));
        assertEquals(List.of("n", "igh", "t"), representations(night));
    }

    @Test void frenchDigraphRemainsOneGraphemeInsideWord() {
        Grapheme ch = grapheme("ch", Language.FR);
        Grapheme a = grapheme("a", Language.FR);
        Grapheme t = grapheme("t", Language.FR);
        Word chat = word("chat", Language.FR, ch, a, t);

        assertEquals(3, chat.graphemes().size());
        assertSame(ch, chat.graphemes().get(0));
        assertEquals(List.of("ch", "a", "t"), representations(chat));
    }

    @Test void wholeFrenchWordCanBeOneMultiLetterGrapheme() {
        Grapheme eau = grapheme("eau", Language.FR);
        Word word = word("eau", Language.FR, eau);

        assertEquals(1, word.graphemes().size());
        assertSame(eau, word.graphemes().get(0));
        assertEquals(List.of("eau"), representations(word));
    }

    @Test void orderedGraphemeSequencePreservesRepeatedGrapheme() {
        Grapheme l = grapheme("l", Language.EN);
        Grapheme e = grapheme("e", Language.EN);
        Word letter = word("letter", Language.EN, l, e, l);

        assertEquals(3, letter.graphemes().size());
        assertSame(l, letter.graphemes().get(0));
        assertSame(l, letter.graphemes().get(2));
        assertEquals(List.of("l", "e", "l"), representations(letter));
    }

    private Grapheme grapheme(String representation, Language language) {
        return new Grapheme(UUID.randomUUID(), representation, language);
    }

    private Word word(String text, Language language, Grapheme... graphemes) {
        return new Word(UUID.randomUUID(), text, language, List.of(graphemes));
    }

    private List<String> representations(Word word) {
        return word.graphemes().stream().map(Grapheme::representation).toList();
    }
}
