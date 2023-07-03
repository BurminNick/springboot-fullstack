import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";

const ProtectedRoute = ({children})=>{

    const navigate = useNavigate();
    const {isCustomerAuthenticated} = useAuth();

    useEffect(()=>{
        if (!isCustomerAuthenticated()) {
            navigate("/")
        }
    })

    return isCustomerAuthenticated() ? children : "";
}

export default ProtectedRoute;