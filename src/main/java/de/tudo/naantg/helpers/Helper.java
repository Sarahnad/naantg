/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.helpers;

import de.tudo.naantg.model.GeneratorModel;
import de.tudo.naantg.model.ObjectModel;

import java.util.List;

public class Helper {

    /**
     * Finds the first object model in the parameter list of the test case model
     * with the given name which data type is equals to the return type or the generic
     * of the optional method return type.
     * @param testName the name of the test case
     * @return the object model for the optional generic
     */
    public static ObjectModel findOptionalObject(GeneratorModel model, String testName) {
        List<ObjectModel> objectModels = model.getParameters(testName);
        List<String> searchTypes = model.getMethodOfCUT(testName).getGenerics();
        String searchType = !searchTypes.isEmpty()? searchTypes.get(0) : "";
        for (ObjectModel objectModel : objectModels) {
            // todo: check if one field has a "param" identifier
            if (objectModel.getDataType().equalsIgnoreCase(searchType)) {
                return objectModel;
            }
        }
        searchType = model.getMethodOfCUT(testName).getReturnType();
        for (ObjectModel objectModel : objectModels) {
            if (objectModel.getDataType().equalsIgnoreCase(searchType)) {
                return objectModel;
            }
        }
        return null;
    }

    /**
     * Returns the modelObject with the same identifier as the given.
     * Return null if it is not found.
     * @param identifier an identifier
     * @param list a list of modelObject
     * @return the found modelObject
     */
    public static ObjectModel findObjectByIdentifier(String identifier, List<ObjectModel> list) {
        for(ObjectModel elem : list) {
            if (identifier.equals(elem.getIdentifier())) {
                return elem;
            }
        }
        return null;
    }

}
