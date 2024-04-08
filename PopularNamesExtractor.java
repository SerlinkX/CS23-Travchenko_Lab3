import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class PopularNamesExtractor {
    public static void main(String[] args) {
        try {
            // Задаємо кількість найпопулярніших імен, яку потрібно вибрати
            int topNamesCount = 5;

            // Завантажуємо XML файл за допомогою DOM парсера
            File xmlFile = new File("Popular_Baby_Names_NY.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Отримуємо список всіх елементів <row>
            NodeList rowList = doc.getElementsByTagName("row");

            // Створюємо мапу для зберігання топових імен для кожної етнічної групи
            Map<String, Map<String, Integer>> topNamesMap = new HashMap<>();

            // Проходимося по кожному елементу <row>
            for (int i = 0; i < rowList.getLength(); i++) {
                Element row = (Element) rowList.item(i);

                // Отримуємо значення етнічності для цього запису
                String ethnicGroup = row.getElementsByTagName("ethcty").item(0).getTextContent();

                // Отримуємо інші дані про дитину
                String name = row.getElementsByTagName("nm").item(0).getTextContent();
                String gender = row.getElementsByTagName("gndr").item(0).getTextContent();
                int count = Integer.parseInt(row.getElementsByTagName("cnt").item(0).getTextContent());
                int rank = Integer.parseInt(row.getElementsByTagName("rnk").item(0).getTextContent());

                // Отримуємо мапу топових імен для поточної етнічної групи
                Map<String, Integer> ethnicTopNamesMap = topNamesMap.getOrDefault(ethnicGroup, new HashMap<>());

                // Додаємо або оновлюємо кількість імен в мапі
                ethnicTopNamesMap.put(name, ethnicTopNamesMap.getOrDefault(name, 0) + count);

                // Оновлюємо мапу топових імен для поточної етнічної групи
                topNamesMap.put(ethnicGroup, ethnicTopNamesMap);
            }

            // Створюємо новий XML документ для збереження топових імен
            DocumentBuilderFactory newFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder newBuilder = newFactory.newDocumentBuilder();
            Document newDoc = newBuilder.newDocument();

            // Створюємо кореневий елемент
            Element rootElement = newDoc.createElement("TopNames");
            newDoc.appendChild(rootElement);

            // Додаємо топові імена для кожної етнічної групи до XML документу
            for (Map.Entry<String, Map<String, Integer>> entry : topNamesMap.entrySet()) {
                String ethnicGroup = entry.getKey();
                Map<String, Integer> ethnicTopNamesMap = entry.getValue();

                // Сортуємо мапу топових імен за кількістю використань
                List<Map.Entry<String, Integer>> sortedTopNames = new ArrayList<>(ethnicTopNamesMap.entrySet());
                sortedTopNames.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

                // Обмежуємо кількість топових імен для поточної етнічної групи
                sortedTopNames = sortedTopNames.subList(0, Math.min(topNamesCount, sortedTopNames.size()));

                // Додаємо кожне топове ім'я до XML документу
                for (Map.Entry<String, Integer> nameEntry : sortedTopNames) {
                    Element nameElement = newDoc.createElement("Name");
                    nameElement.setAttribute("EthnicGroup", ethnicGroup);
                    nameElement.setAttribute("Gender", getGenderForName(nameEntry.getKey(), doc));
                    nameElement.setAttribute("Count", Integer.toString(nameEntry.getValue()));
                    nameElement.setTextContent(nameEntry.getKey());
                    rootElement.appendChild(nameElement);
                }
            }

            // Зберігаємо новий XML файл
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(newDoc);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(new File("TopNames.xml"));
            transformer.transform(source, result);

            System.out.println("Топові імена збережено у файл TopNames.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для отримання статі за іменем
    private static String getGenderForName(String name, Document doc) {
        NodeList rowList = doc.getElementsByTagName("row");
        for (int i = 0; i < rowList.getLength(); i++) {
            Element row = (Element) rowList.item(i);
            String currentName = row.getElementsByTagName("nm").item(0).getTextContent();
            if (currentName.equals(name)) {
                return row.getElementsByTagName("gndr").item(0).getTextContent();
            }
        }
        return ""; // Якщо ім'я не знайдено, повертаємо порожній рядок
    }
}
