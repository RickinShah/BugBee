// import { BrowserRouter } from 'react-router-dom';


// // import {About,Contact,Experience,Feedbacks,Hero,Navbar,Tech,Works} from './src./components';
// import {Navbar,About,Hero,Contact,Experience,Feedbacks,Tech,Works,StarsCanvas} from './components/components';
// const App = () => {
//   return (
//     <BrowserRouter>

//         <div className='relative z-0 bg-primary'>
//           <div className='relative bg-hero-pattern bg-cover bg-no-repeat bg-center'>
//               {/* <Navbar/> */}
//               <Hero/>
//           </div>
//           <About></About>
//           <Experience></Experience>
//           <Tech></Tech>
//           <Works></Works>
//           <Feedbacks></Feedbacks>
//           <div className='relative z-0'>
//               <Contact></Contact>
//               <StarsCanvas></StarsCanvas>

//           </div>
//         </div>
//       </BrowserRouter>     
//   )
// }

// export default App

import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Login from "./Login.jsx";
import SignUp from "./SignUp.jsx";
import ForgotPassword from "./ForgotPassword.jsx";
import OTP from "./OTP.jsx";
import NewPassword from "./NewPassword.jsx";
import UserPage from './UserPage.jsx';
import Accounts from "./Accounts.jsx";
import NewPost from "./NewPost.jsx";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login/>}/>
                <Route path="/auth/signup" element={<SignUp/>}/>
                <Route path="/auth/otp" element={<ForgotPassword/>}/>
                <Route path="/auth/otp/:username" element={<OTP/>}/>
                <Route path="/auth/password/:username" element={<NewPassword/>}/>
                <Route path="/posts" element={<UserPage/>}/>
                <Route path={"/accounts"} element={<Accounts/>}/>
                <Route path={"/NewPost"} element={<NewPost/>}/>
            </Routes>
        </Router>
    );
};

export default App;