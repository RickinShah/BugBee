// import logoo from './assets/assets/man.png'
// import pencil from './assets/assets/pencil.png'
import { useState } from "react";
import { Link } from "react-router-dom";

const Accounts = () => {
  const [Name, setUsername] = useState(localStorage.getItem("name"));
  const [Bio, setBio] = useState(localStorage.getItem("bio"));
  const [ToggleEdit, setToggleEdit] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    bio: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  function ToggleEditfunction() {
    setToggleEdit(!ToggleEdit);
  }
  const updateProfile = async (e) => {
    const response = fetch(`/api/users/settings`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: {
        name: formData.name,
        bio: formData.bio,
        showNsfw: localStorage.getItem("nsfwFlag"),
      },
    });
    if (!response.ok) {
      const result = await response.json();
      console.log("Error from server : ", result);
      return;
    }
    const user = await response.json();
    localStorage.setItem("name", user.name);
    localStorage.setItem("bio", user.bio);
  };

  const toggleNsfw = (e) => {
    localStorage.setItem(
      "nsfwFlag",
      localStorage.getItem("nsfwFlag") === "true" ? false : true,
    );
  };

  return (
    <div className="bg-[#0f0f0f] w-full h-screen flex justify-center ">
      <div className="bg-[#2C2C2C] w-2/4 h-screen absolute">
        <div className="w-full h-1/3 flex flex-col items-center">
          <div className=" w-40 h-40 rounded-full ">
            <div className=" w-full h-full rounded-full flex-col items-center ">
              <img
                src={localStorage.getItem("profilePic")}
                alt="pfp"
                className="w-fit h-full"
              />
            </div>
          </div>
          <div className="flex item-center my-3 font-semibold">
            <button
              className="bg-white rounded-lg p-1 text-black"
              onClick={ToggleEditfunction}
            >
              Edit Profile
            </button>
            <button
              className="w-8 h-full mx-4 bg-red-500 rounded-full"
              onClick={toggleNsfw}
            ></button>
          </div>
        </div>
        <div className="w-full h-2/3 flex justify-center ">
          <div className="w-3/5 h-full flex flex-col">
            <div className="w-full h-1/5 flex m-1">
              <div className="bg-black bg-opacity-15 w-1/2 h-full flex justify-center items-center text-lg font-bold">
                Username
              </div>
              <div className="w-1/2 h-full flex justify-center items-center text-lg font-bold">
                {localStorage.getItem("username")}
              </div>
            </div>
            <div className="w-full h-1/5 flex m-1">
              <div className="bg-black bg-opacity-15 w-1/2 h-full flex justify-center items-center text-lg font-bold">
                Name
              </div>
              <div className="w-1/2 h-full flex justify-center items-center text-lg font-bold">
                <input
                  type="text"
                  onChange={handleChange}
                  name="name"
                  placeholder={Name}
                  className={`${ToggleEdit ? "" : "hidden"} w-full h-full bg-white text-black text-center rounded-lg border border-black`}
                />
                <p className={`${ToggleEdit ? "hidden" : ""}`}>{Name}</p>
              </div>
            </div>
            <div className="w-full h-1/5 flex m-1">
              <div
                name="bio"
                className="bg-black bg-opacity-15 w-1/2 h-full flex justify-center items-center text-lg font-bold "
                onChange={handleChange}
              >
                Bio
              </div>
              <div className="w-1/2 h-full flex justify-center items-center text-lg font-bold">
                <input
                  type="text"
                  name="bio"
                  placeholder={Bio}
                  className={`${ToggleEdit ? "" : "hidden"} w-full h-full bg-white text-black text-center rounded-lg border border-black`}
                />
                <p className={`${ToggleEdit ? "hidden" : ""}`}>{Bio}</p>
              </div>
            </div>
            <div className="w-full h-1/5 flex m-1">
              <div className="w-1/2 h-full flex justify-start items-center text-lg font-bold ">
                <Link to="/forgot-password">
                  <button className="text-yellow-400 hover:text-white">
                    Change Password?
                  </button>
                </Link>
              </div>
              <div className="w-1/2 h-full flex justify-center items-center text-lg font-bold">
                <Link to="/user-page">
                  <button
                    className={`${ToggleEdit ? "" : "hidden"} bg-black w-1/3 p-1 rounded text-white hover:text-black hover:bg-white`}
                    onClick={updateProfile}
                    type="submit"
                  >
                    Apply
                  </button>
                </Link>
              </div>
            </div>
            <div className="w-full h-1/5"></div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default Accounts;
