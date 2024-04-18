import arquivo.Processor;
import arquivo.model.ArticleRecord;
import arquivo.model.SearchEntity;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.repository.SiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest(classes = Processor.class)
@ContextConfiguration
public class Tester {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private SearchEntityRepository searchEntityRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Test
    public void test() {

        String site1 = "https://arquivo.pt/wayback/20010420014947/http://www.expresso.pt/ed1464/r1501.asp";
        String site2 = "https://arquivo.pt/wayback/20010420014947/http://www.expresso.pt/ed1464/r1501.asp?il";


        String text="Atitude mais digna em relação a José Afonso é \"deixá-lo onde está\" Menu Edição do Dia Últimas Notícias País Vida e Futuro Poder Mundo Cidades Cultura Desportos Opinião Dinheiro Bolsa Impostos Economia Evasões DN Ócio DN Life DN Insider Clube Volta ao Mundo Newsletter 1864 Fake News Multimedia Legislativas 2019 Resultados Legislativas 2019 Atitude mais digna em relação a José Afonso é \"deixá-lo onde está\" Francisco Fanhais, amigo e companheiro de canções de José Afonso, lembra que cantor tinha pedido para ser sepultado numa campa rasa DN/Lusa 23 Agosto 2018 — 11:09 O cantor Zeca Afonso. © Arquivo Global Imagens A \"atitude mais digna\" em relação a José Afonso é \"deixá-lo onde está\", porque \"está onde quis estar, em Setúbal e em campa rasa\", defendeu esta quinta-feira o músico Francisco Fanhais, amigo e companheiro de canções de José Afonso. \"Respeitando outras opiniões e a título rigorosamente individual, penso que a atitude mais digna em relação ao José Afonso é deixá-lo onde está\", já que está onde quis estar e \"para morada final, não me parece que a terra seja menos digna do que a pedra polida ou o mármore do Panteão\" , sublinhou Francisco Fanhais, amigo e companheiro de música de José Afonso desde finais da década de 1960. Francisco Fanhais reagia assim à agência Lusa à proposta de trasladação dos restos mortais de José Afonso para o Panteão Nacional, tornada pública pela Sociedade Portuguesa de Autores (SPA) na terça-feira. Para Francisco Fanhais - que gravou pela primeira vez com José Afonso o \"Canto Velho Rumos Novos\", LP editado em 1969 em que Fanhais, então padre, tocava bombo no tema \"S. Macaio\" - a pedra polida ou o mármore talvez sejam mais dignos que a terra \"numa visão sumptuosa da vida, ou da morte\". \"Mas não para uma figura como o Zeca cuja vida foi marcada pela simplicidade, pela fraternidade humana, pela poetização das coisas simples, como diz o seu irmão João Afonso\" , sublinhou o músico \"que se orgulha muito\" de ter gravado o tema \"Grândola, Vila Morena\" com José Afonso. O tema que viria a tornar-se na senha do movimento dos capitães que fizeram a revolução de 25 de Abril de 1974 - e que foi cantado ao vivo, pela primeira vez, por José Afonso, na Galiza - viria a integrar o álbum \"Cantigas do Maio\", gravado em França em 1971 e que foi o segundo trabalho discográfico que Francisco Fanhais gravou com José Afonso, aqui também com José Mário Branco. \"O Zeca continua a convidar-nos à não-resignação e creio que o ouviremos melhor e que a sua mensagem é mais eficaz se soubermos que a sua voz continua a interpelar-nos do sítio onde está. Com terra, ervinhas e flores\" , concluiu Francisco Fanhais, sublinhando sempre que as suas declarações são \"a título individual e enquanto amigo\" e companheiro de caminho de José Afonso e não enquanto presidente da Associação José Afonso. Na quarta-feira, a viúva de José Afonso mostrou-se surpreendida com a proposta da SPA de trasladação dos restos mortais do músico para o Panteão Nacional. Foi \"uma surpresa e vamos decidir\", disse Zélia Afonso, em declarações à agência Lusa. \"A única coisa que eu posso acrescentar é dizer que foi uma surpresa e que o Zeca pediu que fosse [sepultado] em campa rasa, em Setúbal, e eu estou determinada\" a que assim permaneça, ressalvando, no entanto, que há mais pessoas na família. Horas mais tarde, em nota assinada por Pedro Afonso, um dos quatro filhos do músico, a família de José Afonso rejeitaria a proposta da SPA. \"José Afonso rejeitou em vida as condecorações oficiais que lhe haviam sido propostas. Foi, a seu pedido, enterrado em campa rasa e sem cerimónias oficiais, em total coerência com a sua vida e pensamento. Por isso, apesar da meritória intenção que inspira a proposta, é a sua vontade que deve ser respeitada\" , referiu a nota divulgada pela família do artista. Em comunicado divulgado na terça-feira, a SPA defendeu a trasladação dos restos mortais do criador de \"Grândola, Vila Morena\" para o Panteão Nacional, em Lisboa. \u200B\u200B\u200B\u200B\u200B\u200B\u200B \"É este o tributo e é esta homenagem que Portugal deve a quem como mais ninguém o soube cantar em nome dos valores da liberdade, da democracia, da cultura e da cidadania\" , lia-se no comunicado. A SPA afirmou que José Afonso (1929-1987) é \"uma das figuras mais marcantes da história da vida cultural e artística portuguesa\". A cooperativa de autores assumiu \"publicamente o compromisso de lutar por este legítimo e inadiável ato de consagração que deverá coincidir com os 90 anos do nascimento [de José Afonso] e com os 45 anos do 25 de Abril\". Em maio de 1983, o músico foi homenageado pelo município de Coimbra, tendo recebido a Medalha de Ouro da cidade. Na ocasião o então presidente da câmara, Mendes Silva, agradeceu a José Afonso a quem se dirigiu tendo afirmado: \"Volta sempre, a casa é tua\". O compositor retorquiu: \"Não quero converter-me numa instituição, embora me sinta muito comovido e grato pela homenagem\". Também nesse ano, o então Presidente da República, António Ramalho Eanes, atribuiu a José Afonso a Ordem da Liberdade, mas o cantor recusou-se a preencher o formulário. Em 1994, o Presidente da República, Mário Soares, tentou condecorar postumamente José Afonso com a Ordem da Liberdade, mas Zélia Afonso recusou, alegando que o músico não desejou a distinção em vida e também não seria condecorado após a sua morte. A SPA reclama, em nome dos autores portugueses, a trasladação dos restos mortais de José Afonso, sepultados em campa rasa no Cemitério de N. S. da Piedade, em Setúbal. Mais Artigos Outros conteúdos GMG Global Media Group, 2020  © Todos os Direitos Reservados";
        Pattern pattern = Pattern.compile("([^.!?]*(José Afonso|Zeca|Zeca Afonso)[^.!?]*)[.!?]");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String sentence = matcher.group(1);
            System.out.println(">"+sentence);
        }
    }


    private List<String> getRelevantSentences(String text, String name, String aliases) {
        final Pattern pattern;
        if (aliases == null) {
            pattern = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)(" + name + ")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
        } else {
            final String[] namesArrays = aliases.split(",");
            String patternStr = name + "|";
            for (String n : namesArrays) {
                patternStr += n + "|";
            }
            pattern = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)(" + patternStr + ")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
        }

        final Matcher match = pattern.matcher(text);
        final List<String> sentences = new ArrayList<>();
        while (match.find()) {
            sentences.add(match.group(0));
        }
        return sentences;
    }

    private String trimUrl(String originalUrl) {
        originalUrl = originalUrl.split("//")[2];
        if (originalUrl.contains("/amp/")) {
            originalUrl = originalUrl.replace("/amp/", "/");
        }
        return originalUrl;
    }

    private String trimTitle(String title, String siteName, String acronym) {
        boolean containsSiteOnTitle = false;
        if (title.contains("|")) {
            String[] titleParts = title.split("\\|");
            int maxLen = 0;
            int maxLenIndex = 0;
            int i = 0;
            for (String part : titleParts) {
                if (part.length() > maxLen) {
                    maxLen = part.length();
                    maxLenIndex = i;
                    i++;
                }
            }
            title = titleParts[maxLenIndex];
        }
        if (title.contains(siteName)) {
            title = title.replaceAll(siteName, "");
        }
        if (acronym != null && title.contains(acronym)) {
            containsSiteOnTitle = true;
            title = title.replaceAll(acronym, "");
        }
        if (title.contains(siteName.toUpperCase())) {
            containsSiteOnTitle = true;
            title = title.replaceAll(siteName.toUpperCase(), "");
        }
        if (acronym != null && title.contains(acronym.toUpperCase())) {
            containsSiteOnTitle = true;
            title = title.replaceAll(acronym.toUpperCase(), "");
        }
        if (containsSiteOnTitle) {
            if (title.contains(" - ")) {
                title = title.replaceAll(" - ", "");
            }
            if (title.contains(" \\- ")) {
                title = title.replaceAll(" \\– ", "");
            }
            if (title.contains(" \\| ")) {
                title = title.replaceAll(" \\| ", "");
            }
        }
        if (title.startsWith(" ")) {
            title = title.replaceFirst(" ", "");
        }
        return title;
    }
}
