import { useState } from "react";
const Card = ({ post }) => {
  const [upvoteFlag, setUpvoteFlag] = useState(
    post.votedFlag && post.voteStatus,
  );
  const [downvoteFlag, setDownvoteFlag] = useState(
    post.votedFlag && !post.voteStatus,
  );
  const [CommentToggle, setCommentToggle] = useState(false);
  const [CommentData, setCommentData] = useState([]);
  const [content, setContent] = useState("");
  function CommentToggleFunction() {
    setCommentToggle(!CommentToggle);
  }
  const handleChange = (e) => {
    setContent(e.target.value);
    console.log(content);
  };
  console.log(post.postType);
  const votePost = async (voteStatus, postId) => {
    try {
      const response = await fetch(`/api/posts/${postId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          voteStatus: voteStatus,
        }),
      });

      if (!response.ok) {
        const result = await response.json();
        console.log("Error: ", result.message);
      }
    } catch (error) {
      console.error("Error:", error);
    }
  };

  const fetchComments = async (e) => {
    try {
      const postId = post.spostId;
      const offset = 0;
      const size = 100;
      const response = await fetch(
        `/api/posts/${postId}/comments?offset=${offset}&size=${size}`,
      );
      if (!response.ok) {
        const result = await response.json();
        console.log("Error: ", result.message);
        return;
      }
      const result = await response.json();
      console.log(result);
      setCommentData(result);
      CommentToggleFunction();
    } catch (error) {
      console.log("Comment Error: ", error);
    }
  };

  const addComment = async (e) => {
    try {
      const postId = post.spostId;
      const response = await fetch(`/api/posts/${postId}/comments`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          content: content,
        }),
      });
      const result = await response.json();
      console.log(result);
      alert("Comment Added");
      setCommentToggle(false);
    } catch (error) {
      console.log("AddComment Error: ", error);
    }
  };

  const setVote = (postId, voteStatus, voteType) => {
    votePost(voteType, postId);
    if (!post.votedFlag) {
      voteType ? upvoteSetter(postId) : downvoteSetter(postId);
      post.votedFlag = true;
      post.voteStatus = voteType;
      return;
    }
    if (voteType === voteStatus) {
      voteType ? upvoteSetter(postId) : downvoteSetter(postId);
      post.votedFlag = false;
      return;
    }
    downvoteSetter(postId);
    upvoteSetter(postId);
    post.voteStatus = !post.voteStatus;
    post.votedFlag = true;
  };

  function upvoteSetter(postId) {
    upvoteFlag ? (post.upvoteCount -= 1) : (post.upvoteCount += 1);
    setUpvoteFlag(!upvoteFlag);
  }
  function downvoteSetter(postId) {
    downvoteFlag ? (post.downvoteCount -= 1) : (post.downvoteCount += 1);
    setDownvoteFlag(!downvoteFlag);
  }

  const getTag = () => {
    const imageFormats = [".jpg", ".png", ".jpeg"];
    const videoFormats = [".mp4", ".webm"];
    const audioFormats = ["mp3", ".wav", ".ogg"];
    const documentFormats = [".pdf"];
    const fileFormat = post.resource.fileFormat;

    if (imageFormats.includes(fileFormat))
      return `<img src=${post.user.profilePath} alt="Post" className="rounded-full w-8 h-8"/>`;
    if (videoFormats.includes(fileFormat))
      return `<video src=${post.user.profilePath} alt="Post" className="rounded-full w-8 h-8"/>`;
    if (audioFormats.includes(fileFormat))
      return `<audio src=${post.user.profilePath} alt="Post" className="rounded-full w-8 h-8"/>`;
    if (documentFormats.includes(fileFormat))
      return `<document src=${post.user.profilePath} alt="Post" className="rounded-full w-8 h-8"/>`;
  };

  return (
    <div className=" bg-[#1D1D1D] w-full h-[500px] m-1 my-5 rounded-lg p-2 ">
      <div className="h-10 flex ">
        <img
          src={localStorage.getItem("profilePic")}
          alt="Post"
          className="rounded-full w-8 h-8"
        />
        {getTag}
        <h3 className="mx-4">{post.user.username}</h3>
      </div>
      <hr />
      <br />
      <div id={post.spostId} className="flex w-full h-4/5 ">
        <div
          className={`${CommentToggle ? "" : "hidden"} w-11/12 h-fit rounded-lg bg-black bg-opacity-90 border-[2px] absolute`}
        >
          {CommentData.map((CommentData, index) => (
            <div className="w-full h-full flex flex-col">
              <div className="border rounded m-2 border-opacity-10 p-2 ">
                <div className="w-full h-7 flex items-center p-2 my-1">
                  <img
                    src={CommentData.user.profilePath}
                    alt="Post"
                    className="rounded-full w-6 h-6"
                  />
                  <h3 className="mx-4 font-medium">
                    {CommentData.user.username}
                  </h3>
                </div>
                <div className="w-full h-fit bg-[#d0d0d0] my-1 flex justify-between rounded-md">
                  <p className="text-black p-1 font-semibold">
                    {CommentData.content}
                  </p>
                </div>
              </div>
            </div>
          ))}
          <div className="flex flex-col justify-center items-center m-3">
            <input
              type="text"
              className="bg-white w-2/3 text-black font-bold p-2 m-2"
              placeholder="Type to Comment"
              onChange={handleChange}
            />
            <div className="w-2/3 h-fit m-2 flex">
              <button
                className="w-1/2 h-full bg-yellow-400 rounded-lg p-2 text-black font-bold mx-1
                                                    hover:bg-yellow-500"
                onClick={addComment}
              >
                Comment
              </button>
              <button
                className="w-1/2 h-full bg-red-500 rounded-lg p-2 text-black font-bold mx-1
                                                    hover:bg-red-600"
                onClick={CommentToggleFunction}
              >
                Close
              </button>
            </div>
          </div>
        </div>
        <div className="flex h-full w-2/3 ">
          <img
            src={`${post.postType}`}
            alt="Post"
            className={`${post.resource.nsfwFlag && localStorage.getItem("nsfwFlag") === "false" ? "blur-lg" : "blur-none"} rounded-2xl w-fit h-full`}
          />
        </div>
        <div className="flex h-full w-1/3 ">
          <p className="mx-5 text-2xl">{post.content}</p>
        </div>
      </div>
      <div>
        <div className=" h-full w-1/2 flex justify-start">
          <div className="mx-3 w-1/4 h-full  flex justify-start items-center">
            <div className="flex w-fit h-full">
              <button
                className={`${!upvoteFlag ? "bg-[#3F3F3F] hover:bg-yellow-400 text-yellow-400 hover:text-[#3F3F3F]" : "bg-yellow-400 text-[#3F3F3F] hover:bg-[#3F3F3F] hover:text-yellow-400"} p-1 my-1 m-1 w-10 rounded-full `}
                onClick={() => setVote(post.spostId, post.voteStatus, true)}
              >
                UV
              </button>
              <p className="m-2 mx-5 font-bold text-yellow-400">
                {post.upvoteCount}
              </p>
            </div>
          </div>
          <div className="mx-3 w-1/4 h-full  flex justify-start items-center">
            <div className="flex w-fit h-full">
              <button
                className={`${!downvoteFlag ? "bg-[#3F3F3F] hover:bg-yellow-400 text-yellow-400 hover:text-[#3F3F3F]" : "bg-yellow-400 text-[#3F3F3F] hover:bg-[#3F3F3F] hover:text-yellow-400"} p-1 my-1 m-1 w-10 rounded-full `}
                onClick={() => setVote(post.spostId, post.voteStatus, false)}
              >
                DV
              </button>
              <p className="m-2 mx-5 font-bold text-yellow-400">
                {post.downvoteCount}
              </p>
            </div>
          </div>
          <div className="mx-3 w-1/4 h-full  flex justify-start items-center">
            <div className="flex w-fit h-full">
              <button
                className="bg-[#3F3F3F] p-1 my-1 m-5 w-10 rounded-full hover:bg-yellow-400 text-yellow-400 hover:text-[#3F3F3F]"
                onClick={fetchComments}
              >
                C
              </button>
            </div>
          </div>
          <div className="mx-3 w-1/4 h-full flex justify-start items-center">
            <div className="flex w-fit h-full">
              <button className="bg-[#3F3F3F] p-1 my-1 m-5 w-10 rounded-full hover:bg-yellow-400 text-yellow-400 hover:text-[#3F3F3F]">
                R
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Card;
