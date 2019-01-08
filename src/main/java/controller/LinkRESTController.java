package controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.monitorjbl.json.JsonView;
import model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import repository.LinkRepo;
import util.ShortLinkGeneratorUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.monitorjbl.json.Match.match;

@RestController
public class LinkRESTController
{
    @Autowired
    private LinkRepo linkRepo;

    @Autowired
    private ObjectMapper mapper;

    private static final String beforeLink = "/l/";


    @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayNode getAllLinksPageCount(@RequestParam("page") int page,
                                 @RequestParam("count") int count)
    {
        List<Link> allLinks = linkRepo.getAllLinks();

        ShortLinkGeneratorUtils.checkMinMaxCount(1, 100, count);
        int maxPageNumber = ShortLinkGeneratorUtils.returnMaxPageAvailable(allLinks.size(), page, count);

        int indexFrom = (page - 1) * count;
        int indexTo = indexFrom + count;

        return ShortLinkGeneratorUtils.getSublist(mapper,
                ShortLinkGeneratorUtils.formLinksWithRank(mapper, allLinks), indexFrom, indexTo, page, maxPageNumber);
    }

    @GetMapping(value = "/stats/{shortLink}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ObjectNode getLinkStatsShortName(@PathVariable("shortLink") String shortLink)
    {
        Link link = ShortLinkGeneratorUtils.checkLinkExistence(linkRepo, beforeLink + shortLink);
        return ShortLinkGeneratorUtils.getLinkWithRank(mapper,
                linkRepo.getAllLinks(), link);
    }

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public Link generateShortLink(@RequestBody Link link) throws URISyntaxException, IOException
    {
        String originalLink = link.getOriginal();
        ShortLinkGeneratorUtils.isValid(originalLink);

        String finalShortName = ShortLinkGeneratorUtils.generateShortLink();

        while (linkRepo.getLinkByShortName(finalShortName) != null)
        {
            finalShortName = ShortLinkGeneratorUtils.generateShortLink();
        }

        Link newLink = new Link();
        newLink.setOriginal(ShortLinkGeneratorUtils.getUrlWithoutParameters(originalLink));
        newLink.setLink(beforeLink + finalShortName);
        Link createdLink = linkRepo.saveLink(newLink);

        String createdLinkLinkOnly = mapper.writeValueAsString(JsonView.with(createdLink).
                onClass(Link.class, match()
                        .exclude("*")
                        .include("link")
                ));

        System.out.println(createdLinkLinkOnly);

        return mapper.readValue(createdLinkLinkOnly, Link.class);
    }

    @GetMapping(value = "/l/{link}")
    public RedirectView redirectToOriginal(@PathVariable("link") String shortLink)
    {
        Link link = ShortLinkGeneratorUtils.checkLinkExistence(linkRepo, beforeLink + shortLink);
        link.setCount(link.getCount() + 1);
        linkRepo.saveLink(link);
        return new RedirectView(link.getOriginal());
    }

}
