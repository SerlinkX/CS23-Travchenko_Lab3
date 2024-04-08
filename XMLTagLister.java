import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XMLTagLister {
    public static void main(String[] args) {
        try {
            // Створення фабрики для SAX парсера
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // Створення об'єкту SAX парсера
            SAXParser saxParser = factory.newSAXParser();

            // Ініціалізуємо обробник подій SAX
            DefaultHandler handler = new DefaultHandler() {
                // Викликається при знаходженні відкриваючого тегу
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    System.out.println("Тег: " + qName);
                }
            };

            // Вказуємо обробник подій SAX парсеру
            saxParser.parse("Popular_Baby_Names_NY.xml", handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
