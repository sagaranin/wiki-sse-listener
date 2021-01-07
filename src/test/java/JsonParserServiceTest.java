import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.larnerweb.wikisselistener.entitiy.WikiEvent;
import ru.larnerweb.wikisselistener.service.JSONParserService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JsonParserServiceTest {

    @Autowired
    JSONParserService jsonParserService;

    @Test
    public void testParsingIsCorrect() throws IOException {
        String data = "{\"$schema\":\"/mediawiki/recentchange/1.0.0\",\"meta\":{\"uri\":\"https://www.wikidata.org/wiki/Q7796601\",\"request_id\":\"X-bNIwpAMOEAAhoHFVgAAACS\",\"id\":\"dded3bc5-e9c4-419b-a38a-4b2205d8f627\",\"dt\":\"2021-01-07T08:58:11Z\",\"domain\":\"www.wikidata.org\",\"stream\":\"mediawiki.recentchange\",\"topic\":\"eqiad.mediawiki.recentchange\",\"partition\":0,\"offset\":2854368884},\"id\":1379742703,\"type\":\"edit\",\"namespace\":0,\"title\":\"Q7796601\",\"comment\":\"/* wbcreateclaim-create:1| */ [[Property:P646]]: /m/027x_tz\",\"timestamp\":1610009891,\"user\":\"Lockal\",\"bot\":false,\"minor\":false,\"patrolled\":true,\"length\":{\"old\":4709,\"new\":5058},\"revision\":{\"old\":1197445331,\"new\":1336461446},\"server_url\":\"https://www.wikidata.org\",\"server_name\":\"www.wikidata.org\",\"server_script_path\":\"/w\",\"wiki\":\"wikidatawiki\",\"parsedcomment\":\"\u200E<span dir=\\\"auto\\\"><span class=\\\"autocomment\\\">Создано заявление: </span></span> <a href=\\\"/wiki/Property:P646\\\" title=\\\"Property:P646\\\">Property:P646</a>: /m/027x_tz\"}";
        WikiEvent event = jsonParserService.parse(data);
        System.out.println(event);
    }

}
