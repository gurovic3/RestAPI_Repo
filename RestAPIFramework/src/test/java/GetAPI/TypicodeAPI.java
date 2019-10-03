////////////////////////////////////////////////////////////////////////////////////
// API Automation : To verify Rest API calls on https://jsonplaceholder.typicode.com
// 
// Created By : Marianna Gurovich on 10/02/2019
////////////////////////////////////////////////////////////////////////////////////

package GetAPI;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;

import java.util.List;
import java.util.Map;

public class TypicodeAPI {
	@BeforeTest
	public void setBaseUri() {
		RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
	}

	@Test
	public void testGetPostsStatusCodeAndContentType() {
		given()
			.get("/posts")
		.then()
			.assertThat()
				.statusCode(200)
			.and()
				.contentType(ContentType.JSON);
	}

	@Test
	public void testGetPostsNonEmptyResponse() {
		Response res = 
			given()
				.get("/posts")
			.then()
				.extract()
				.response();
		
		res.then()
			.assertThat()
				.body("$", Matchers.notNullValue());
	}
	
	@Test
	public void testGetPostsResponse() {
		List<Map<String, Object>> posts = 
			given()
				.get("/posts")
				.as(new TypeRef<List<Map<String, Object>>>() {});
		
		// validation
		Assert.assertEquals(posts.size(), 100, "Invalid number of posts received!");
		for (int i = 0; i < posts.size(); i++) {
			Map<String, Object> post = posts.get(i);
			Assert.assertEquals(post.get("id"), i+1, "Invalid post id received in post object!");
			Assert.assertNotNull(post.get("userId"), "'userId' field can't be null!");
			Assert.assertTrue(post.get("userId").toString().length() > 0, "'userId' field can't be empty!");
			Assert.assertNotNull(post.get("title"), "'title' field can't be null!");
			Assert.assertTrue(post.get("title").toString().length() > 0, "'title' field can't be empty!");
			Assert.assertNotNull(post.get("body"), "'body' field can't be null!");
			Assert.assertTrue(post.get("body").toString().length() > 0, "'body' field can't be empty!");
		}
	}
	
	@Test
	public void testGetPostById() {
		Response res = 
			given()
				.get("/posts/1")
			.then()
				.extract()
				.response();
		
		// validation
		res.then().body("id", Matchers.is(1));
		res.then().body("userId", Matchers.is(1));
		res.then().body("title", Matchers.notNullValue());
		res.then().body("body", Matchers.notNullValue());
	}
	
	@Test
	public void testGetPostByInvalidId() {
		given()
			.get("/posts/200")
		.then()
            .statusCode(404);
	}
	
	@Test
	public void testGetCommentsStatusCodeAndContentType() {
		given()
			.param("postId", "1")
			.get("/comments")
		.then()
			.assertThat()
			.statusCode(200)
		.and()
			.contentType(ContentType.JSON);
	}
	
	@Test
	public void testGetCommentsResponse() {
		List<Map<String, Object>> comments = 
			given()
				.param("postId", "1")
				.get("/comments")
				.as(new TypeRef<List<Map<String, Object>>>() {});
		
		// validation
		Assert.assertTrue(comments.size() > 0, "No comments received for post 1!");
		for (Map<String, Object> comment : comments) {
			Assert.assertEquals(comment.get("postId"), 1, "Invalid postId received in comment object!");
			Assert.assertNotNull(comment.get("id"), "'id' field can't be null!");
			Assert.assertTrue(comment.get("id").toString().length() > 0, "'id' field can't be empty!");
			Assert.assertNotNull(comment.get("name"), "'name' field can't be null!");
			Assert.assertTrue(comment.get("name").toString().length() > 0, "'name' field can't be empty!");
			Assert.assertNotNull(comment.get("email"), "'email' field can't be null!");
			Assert.assertTrue(comment.get("email").toString().length() > 0, "'email' field can't be empty!");
			Assert.assertNotNull(comment.get("body"), "'body' field can't be null!");
			Assert.assertTrue(comment.get("body").toString().length() > 0, "'body' field can't be empty!");
		}
	}
	
	@Test
	public void testGetUserPostsStatusCodeAndContentType() {
		given()
			.param("userId", "1")
			.get("/posts")
		.then()
			.assertThat()
			.statusCode(200)
		.and()
			.contentType(ContentType.JSON);
	}
	
	@Test
	public void testGetUserPosts() {
		List<Map<String, Object>> posts = 
			given()
				.param("userId", "1")
				.get("/posts")
				.as(new TypeRef<List<Map<String, Object>>>() {});
		
		// tests
		Assert.assertTrue(posts.size() > 0, "No posts received for user 1!");
		for (Map<String, Object> comment : posts) {
			Assert.assertEquals(comment.get("userId"), 1, "Invalid userId received in post object!");
			Assert.assertNotNull(comment.get("id"), "'id' field can't be null!");
			Assert.assertTrue(comment.get("id").toString().length() > 0, "'id' field can't be empty!");
			Assert.assertNotNull(comment.get("title"), "'title' field can't be null!");
			Assert.assertTrue(comment.get("title").toString().length() > 0, "'title' field can't be empty!");
			Assert.assertNotNull(comment.get("body"), "'body' field can't be null!");
			Assert.assertTrue(comment.get("body").toString().length() > 0, "'body' field can't be empty!");
		}
	}
	
	@Test
    public void testPostRequest() {
		Response res = 
			given()
				.contentType(ContentType.JSON)
				.header("Content-type", "application/json; charset=UTF-8")
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.body("{\"title\": \"foo\",\"body\": \"bar\",\"userId\": 7}")
	            .post("/posts")
            .then()
	            .statusCode(201)
	            .extract()
	            .response();
		
		// validation
		res.then().body("id", Matchers.is(101));
		res.then().body("userId", Matchers.is(7));
		res.then().body("title", Matchers.is("foo"));
		res.then().body("body", Matchers.is("bar"));
    }
	
	@Test
    public void testPostRequestWithInvalidId() {
		given()
			.contentType(ContentType.JSON)
			.header("Content-type", "application/json; charset=UTF-8")
			.header("Accept", ContentType.JSON.getAcceptHeader())
			.body("{\"id\": 200,\"title\": \"foo\",\"body\": \"bar\",\"userId\": 7}")
            .post("/posts/1")
        .then()
            .statusCode(404);
    }
	
	@Test
    public void testPutRequestFull() {
		Response res = 
			given()
				.contentType(ContentType.JSON)
				.header("Content-type", "application/json; charset=UTF-8")
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.body("{\"id\": 1,\"title\": \"foo\",\"body\": \"bar\",\"userId\": 100}")
	            .put("/posts/1")
            .then()
	            .statusCode(200)
	            .extract()
	            .response();
		
		// validation
		res.then().body("id", Matchers.is(1));
		res.then().body("title", Matchers.is("foo"));
		res.then().body("body", Matchers.is("bar"));
		res.then().body("userId", Matchers.is(100));
    }
	
	@Test
    public void testPutRequestPartial() {
		Response res = 
			given()
				.get("/posts/1")
			.then()
				.extract()
				.response();
		String oldTitle = res.jsonPath().getString("title");
		String newTitle = oldTitle.substring(0, 5);
			
		res = given()
				.contentType(ContentType.JSON)
				.header("Content-type", "application/json; charset=UTF-8")
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.body(String.format("{\"title\": \"%s\",\"userId\": 100}", newTitle))
	            .put("/posts/1")
            .then()
	            .statusCode(200)
	            .extract()
	            .response();
		
		// validation
		res.then().body("id", Matchers.is(1));
		res.then().body("title", Matchers.is(newTitle));
		res.then().body("body", Matchers.nullValue());
		res.then().body("userId", Matchers.is(100));
    }
	
	@Test
    public void testPutRequestIdNotFound() {
		given()
			.contentType(ContentType.JSON)
			.header("Content-type", "application/json; charset=UTF-8")
			.header("Accept", ContentType.JSON.getAcceptHeader())
			.body("{\"id\": 200,\"title\": \"foo\",\"body\": \"bar\",\"userId\": 100}")
            .put("/posts")
        .then()
            .statusCode(404);
    }
	
	@Test
    public void testPatchRequest() {
		Response res = 
			given()
				.contentType(ContentType.JSON)
				.header("Content-type", "application/json; charset=UTF-8")
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.body("{\"title\": \"foo\",\"body\": \"bar\",\"userId\": 100}")
	            .patch("/posts/1")
            .then()
	            .statusCode(200)
	            .extract()
	            .response();
		
		// validation
		res.then().body("id", Matchers.is(1));
		res.then().body("title", Matchers.is("foo"));
		res.then().body("body", Matchers.is("bar"));
		res.then().body("userId", Matchers.is(100));
    }
	
	@Test
    public void testDeleteRequest() {
		given()
			.delete("/posts/1")
        .then()
            .statusCode(200);
    }


//	@Test
//	public void testPostRequestWithSameId() {
//		given()
//			.contentType(ContentType.JSON)
//			.header("Content-type", "application/json; charset=UTF-8")
//			.header("Accept", ContentType.JSON.getAcceptHeader())
//			.body("{\"id\": 200,\"title\": \"foo\",\"body\": \"bar\",\"userId\": 7}")
//			.post("/posts/1")
//		.then()
//			.statusCode(409);
//	}

}
