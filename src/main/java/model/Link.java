package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Link
{
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer id;
    private Integer count;
    private String original;
    private String link;


    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }

    public String getOriginal()
    {
        return original;
    }

    public void setOriginal(String original)
    {
        this.original = original;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String toString()
    {
        return "this is the link with id " + id + " original " + original + " short " + link + " and counter " + count;
    }
    public Link()
    {

    }
}
