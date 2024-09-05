import { Link, useLocation, useNavigate, useNavigation } from "react-router-dom";
import { useState } from 'react';

const ForgotPassword = () =>{
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: ''
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
        const response = await fetch('https://localhost/api/auth/otp', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        const result = await response.json(); // Parsing JSON response
        if(response.ok !== true) {
            console.log('Success:', result.message);
        }
        else {
            navigate(`/auth/otp/${result.message}`);
            console.log(result.message);
        }
    } catch (error) {
      console.error('Error:', error);
    }
    };

    return(
        <section className="w-full h-screen bg-[#1E1E1E] flex justify-center">
            <div className="relative">

           

            <div className="font-semibold">
                <h1 className="text-8xl p-5 m-5">Forgot <span className="text-yellow-400 font-extrabold">Password?</span></h1>
            </div>
            <br />
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
                                <span className="font-semibold m-5 text-black text-4xl"> Enter your Email </span>
                                <br /><br />
                                <form onSubmit={handleSubmit}>
                                <input type="text" name="username" value={formData.username} onChange={handleChange} placeholder="Email" className="m-5 h-8 p-2 w-72 flex rounded bg-white text-black placeholder-black"/>
                                
                                {/* <Link to="/otp"> */}
                                <button className="flex justify-center align-middle w-72 h-10 p-2 m-5 rounded-lg bg-[#1E1E1E] 
                                    hover:bg-[#564a02] font-medium " type="submit">Next</button>
                                {/* </Link> */}
                                </form>
                            </div>
                            

                        </div>
                    </div>
                </div>
                </div>
            </div>
        </section>
    )

}

export default ForgotPassword;