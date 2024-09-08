import { Link } from 'react-router-dom';
import { useState } from 'react';


const Page = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: ''
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
        const response = await fetch('/api/auth/login', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        const result = await response.json(); // Parsing JSON response
        if(response.ok != true) {
            console.log('Success:', result.message);
        }
        else {
            console.log(result.id_token);
        }
    } catch (error) {
      console.error('Error:', error);
    }
    };

    return(
        
        <section className="relative bg-white w-full h-screen ">
            
            <div className="flex justify-evenly">
                <div className="relative w-2/3 bg-[#1E1E1E] h-screen">
                    <div className="font-semibold">
                        <h1 className="text-8xl p-5 m-5">Welcome to <span className="text-yellow-400 font-extrabold">Bugbee!</span></h1>
                    </div>
                    <div cla>

                        <div className="p-10 w-2/3 ">
                            <p>
                                <span className="text-5xl font-bold">Post, Share <br />And More!!</span>
                                <br /><br />
                                BugBee is a social media platform designed to facilitate interactive discussions, multimedia sharing and community engagement
                            
                            </p>
                        </div>
                        
                    </div>
                    

                    
                </div>


                <div className="flex bg-yellow-400 h-screen w-1/3 justify-center align-middle">
                    <div className="bg-red w-96 relative ">    
                        <div><br /><br /><br /><br /></div>                   
                        <div>
                            <div className=" text-5xl flex align-middle justify-center p-8">
                                <h3 className="font-semibold text-black underline">Sign in</h3>
                            </div>
                            <div className="flex justify-center ">
                                <form onSubmit={handleSubmit}>
                                    <input type="text" name='username' value={formData.username} onChange={handleChange} placeholder="Username" className=" p-2 m-6 h-8 w-72 flex rounded bg-white text-black placeholder-black"/>
                                    <input type="password" name='password' value={formData.password} onChange={handleChange} placeholder="Password" className=" p-2 m-6 h-8 w-72 flex rounded bg-white text-black placeholder-black"/>
                                    <button className="flex justify-center align-middle w-72 h-10 p-2 m-6 rounded-lg bg-[#1E1E1E] 
                                    hover:bg-[#564a02] font-medium" type='submit'>Sign in</button>
                                </form>

                            </div>
                            <div className="flex justify-center">
                                <div >
                                    <p className="flex align-middle justify-center text-3xl font-semibold">or</p>
                                    <Link to="/auth/otp">
                                    <button className="font-medium flex justify-center align-middle w-72 h-10 p-2 m-6 rounded-lg bg-[#1E1E1E]
                                    hover:bg-[#564a02]">Forgot Passoword?</button>
                                    </Link>
                                    <p className="flex align-middle justify-center font-medium underline">Dont have an account?  <Link to="/auth/signup" className="text-[#1E1E1E] font-bold hover:text-white " >Sign-up</Link> </p>
                                </div>

                            </div>

                            
                        </div>    
                        
                    </div>
                    
                </div>
            </div>
            
        </section>
    )
}

export default Page;