package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.Link;
import org.apache.commons.text.RandomStringGenerator;
import repository.LinkRepo;
import util.exception.ResourceNotFoundException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class ShortLinkGeneratorUtils
{
    private ShortLinkGeneratorUtils() {}

    public static String generateShortLink()
    {
        char [][] digitsAzAZ = {{'a', 'z'}, {'A','Z'}, {'0', '9'}};
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange(digitsAzAZ)
                .build();
        return generator.generate(7);
    }

    public static void isValid(String url)
    {
        try {
            new URL(url).toURI();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Illegal URL");
        }
    }

    public static String getUrlWithoutParameters(String url) throws URISyntaxException
    {
        URI uri = new URI(url);
        return new URI(uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null,
                uri.getFragment()).toString();
    }

    public static Link checkLinkExistence(LinkRepo linkRepo, String link)
    {
        Link linkInDb = linkRepo.getLinkByShortName(link);

        if(linkInDb == null)
        {
            throw new ResourceNotFoundException();
        }
        else return linkInDb;
    }

    public static ArrayNode formLinksWithRank(ObjectMapper mapper, List<Link> allLinks)
    {
        ArrayNode arrayNode = mapper.createArrayNode();

        for(int i = 0; i < allLinks.size(); i++)
        {
            Link particularLink = allLinks.get(i);
            ObjectNode linkWithRank = mapper.createObjectNode();
            putFieldsWithRankInLink(linkWithRank, particularLink, i + 1);

            arrayNode.add(linkWithRank);
        }

        return arrayNode;
    }

    public static ObjectNode getLinkWithRank(ObjectMapper mapper, List<Link> allLinks,
                                             Link originalLink)
    {
        for(int i = 0; i < allLinks.size(); i++)
        {
            Link particularLink = allLinks.get(i);

            if(particularLink.getLink().equals(originalLink.getLink()))
            {
                ObjectNode finalRankedLink = mapper.createObjectNode();
                putFieldsWithRankInLink(finalRankedLink, particularLink, i + 1);
                return finalRankedLink;
            }
        }
        return null;
    }

    private static void putFieldsWithRankInLink(ObjectNode jsonLinkObj, Link link, int rankPosition)
    {
        jsonLinkObj.put("link", link.getLink());
        jsonLinkObj.put("original", link.getOriginal());
        jsonLinkObj.put("rank", rankPosition);
        jsonLinkObj.put("count", link.getCount());
    }


    public static ArrayNode getSublist(ObjectMapper mapper,
                                       ArrayNode originalArray, int indexFrom, int indexTo, int currentPage, int maxPageNum)
    {
        ArrayNode subList = mapper.createArrayNode();

        if(currentPage == maxPageNum)
        {
            indexTo = originalArray.size();
        }

        for(int i = indexFrom; i < indexTo; i++)
        {
           subList.add(originalArray.get(i));
        }
        return subList;
    }

    public static void checkMinMaxCount(int minCount, int maxCount, int currentCount)
    {
        if(currentCount < minCount
                || currentCount > maxCount)
        {
            throw new IllegalArgumentException("Counter is beyond allowed values");
        }
    }

    public static int returnMaxPageAvailable(int allLinksNum,
                                             int page, int count)
    {
        int maxPageNumber = (int) Math.ceil(
                (double) allLinksNum / count);

        if(maxPageNumber < page)
        {
            throw new IllegalArgumentException("This page doesn't exist for the counter " + count +
                    "; Max page number is " + maxPageNumber);
        }

        return maxPageNumber;
    }
}
