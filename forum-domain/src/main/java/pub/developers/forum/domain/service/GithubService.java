package pub.developers.forum.domain.service;



/**
 * @author Qiangqiang.Bian
 * @create 2021/5/15
 * @desc
 **/
public interface GithubService {

    com.fasterxml.jackson.databind.JsonNode getUserInfo(String code);

}