import {useState} from "react";
import {useNavigate, useParams} from "react-router-dom";

const OTP = () => {
    // const location = useLocation();
    const {username} = useParams();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        otp: '',
        email: username
    });

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };


    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await fetch('/api/auth/otp/' + username, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
            const result = await response.json(); // Parsing JSON response
            if (response.ok != true) {
                console.log('Success:', result.message);
            } else {
                navigate(`/auth/password/${username}`);
                console.log(result.message);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <section className="w-full h-screen bg-[#1E1E1E] flex justify-center">
            <div className="relative">


                <div className="font-semibold">
                    <h1 className="text-8xl p-5 m-5">Forgot <span
                        className="text-yellow-400 font-extrabold">Password?</span></h1>
                </div>
                <br/>
                <div className="flex justify-center align-middle">
                    <div className="
                                absolute
                              bg-yellow-400
                                w-2/4
                                h-max
                                shadow-2xl
                                shadow-yellow-400

                ">

                        <div className="flex justify-center align-middle">
                            <div className="relative m-5 p-2 ">
                                <div className="m-5">
                                    <span className="font-semibold m-5 text-black text-4xl"> Enter OTP </span>
                                    <br/><br/>
                                    <form onSubmit={handleSubmit}>
                                        <input type="text" name="otp" onChange={handleChange} placeholder="OTP"
                                               className="m-5 h-8 p-2 w-72 flex rounded text-black bg-white placeholder-black"/>

                                        {/* <Link to="/new-password"> */}
                                        <button className="flex justify-center align-middle w-72 h-10 p-2 m-5 rounded-lg bg-[#1E1E1E]
                                    hover:bg-[#564a02] font-medium " type="submit">Next
                                        </button>
                                    </form>
                                    {/* </Link> */}
                                </div>


                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    )

}

export default OTP;