import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

function NewPassword() {
  const navigate = useNavigate();
  const { username } = useParams();
  const [formData, setFormData] = useState({
    username: username,
    password: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("/api/auth/otp/" + username, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });
      const result = await response.json(); // Parsing JSON response
      if (response.ok !== true) {
        console.log("Success:", result.message);
      } else {
        navigate(`/`);
        console.log(result.message);
      }
    } catch (error) {
      console.error("Error:", error);
    }
  };

  return (
    <section className="w-full h-screen bg-[#1E1E1E] flex justify-center">
      <div className="relative">
        <div className="font-semibold">
          <h1 className="text-8xl p-5 m-5">
            Forgot{" "}
            <span className="text-yellow-400 font-extrabold">Password?</span>
          </h1>
        </div>
        <br />
        <div className="flex justify-center align-middle">
          <div
            className="
                                absolute
                              bg-yellow-400
                                w-2/4
                                h-max
                                shadow-2xl
                                shadow-yellow-400

                "
          >
            <div className="flex justify-center align-middle">
              <div className="relative m-5 p-2 ">
                <div className="m-5">
                  <br />
                  <br />
                  <br />
                  <form onSubmit={handleSubmit}>
                    <input
                      type="password"
                      name="password"
                      placeholder="New Password"
                      onChange={handleChange}
                      className="m-5 h-8 p-2 w-72 flex rounded text-black bg-white placeholder-black"
                    />
                    <input
                      type="password"
                      placeholder="Confirm Password"
                      className="m-5 h-8 p-2 w-72 flex rounded bg-white text-black placeholder-black"
                    />

                    <button
                      className="flex justify-center align-middle w-72 h-10 p-2 m-5 rounded-lg bg-[#1E1E1E]
                                    hover:bg-[#564a02] font-medium "
                      type="submit"
                    >
                      Next
                    </button>
                    <br />
                    <br />
                    <br />
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

export default NewPassword;
