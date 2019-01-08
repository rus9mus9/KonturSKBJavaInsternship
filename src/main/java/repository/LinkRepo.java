package repository;

import model.Link;

import java.util.List;

public interface LinkRepo
{
    Link getLinkById(int linkId);
    Link getLinkByShortName(String shortName);
    Link saveLink(Link link);
    List<Link> getAllLinks();
}
