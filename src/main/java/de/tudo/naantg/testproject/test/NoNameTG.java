/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.test;

import de.tudo.naantg.annotations.EntityTG;
import de.tudo.naantg.testproject.scheinboot.ScheinJob;

/**
 * Test generation interface to test the methods of {@link de.tudo.naantg.testproject.scheinboot.ScheinJob}.
 * <p>Tests the generation for a class with a different name as the default name for the test-generation class.</p>
 */
@EntityTG(ScheinJob.class)
public interface NoNameTG {


}
