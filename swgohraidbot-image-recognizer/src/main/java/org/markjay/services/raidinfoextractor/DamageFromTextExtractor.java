package org.markjay.services.raidinfoextractor;

import org.markjay.exceptions.RaidInfoExtractionException;

import java.util.logging.Logger;

/**
 * @author <a href="mailto:mark.jay.mk@gmail.com">mark jay</a>
 * @since 1/2/19 12:20 AM
 */
public class DamageFromTextExtractor {

    private static final Logger log = Logger.getLogger(DamageFromTextExtractor.class.getName());

    long extractDamageFromText(String text) throws RaidInfoExtractionException {
        log.info("-------------------------------------------");
        log.info("Extracting from a word");
        log.info("original text was = '" + text + "'");
        
        String lineWithDamage = findLineWithDamage(text);
        log.info("Found line with damage = " + lineWithDamage);

        String damageFound = lineWithDamage.replaceAll("^[^0-9]*", "");
        String damageAsString = damageFound.replaceAll(",", "").replaceAll("\\.", "");
        log.info("Damage as string was = " + damageAsString);

        if (!damageAsString.matches("[0-9][0-9]*")) {
            throw new RaidInfoExtractionException("Expected to find damage as a long value but found '" + damageAsString + "'");
        }

        log.info("Damage as string is found to be = " + damageAsString);
        return Long.parseLong(damageAsString);
    }

    private String findLineWithDamage(String text) {
        for (String line : text.split("\n")) {
            if (line.contains("Damage")) {
                return line;
            }
        }
        throw new RuntimeException("Line with a word 'Damage' was not found");
    }
}
