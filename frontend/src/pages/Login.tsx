import React from "react";
import { useTranslation } from "react-i18next";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';
import { User } from "../models/UserTypes"
import Background from "../components/Background";
import {loginUser} from "../services/auth";



const LoginPage = (props: {doLogin: any}) => {
    const { t } = useTranslation();

    const user: User = {} as User; //This is mocking an user to save the information and should be passed to the api call 
    const [email, setEmail] = React.useState("");
    const [password, setPassword] = React.useState("");

    function login(email: string, password: string){
       loginUser(email, password).then((res) => props.doLogin());
    }

    return (
        <div className="section section-hero section-shaped">
            <Background />

            <div className="container">
                <div className="white-pill">
                    <div className="d-flex justify-content-center">
                        <div className="h1 text-primary">{t("logIn")}</div>
                    </div>
                    <hr/>

                    <div>
                        {/* <%--Email--%> */}
                        <div className="form-group mt-3">
                            <p className="text-black">{t("email")}</p>
                            <input className="form-control" type="email" id="email" value={email} placeholder={t("placeholder.email")} onChange={(e) => setEmail(e.target.value)} />
                        </div>

                        {/* <%--Password--%> */}
                        <div className="form-group mt-3">
                            <p className="text-black">{t("password.message")}</p>
                            <input className="form-control" type="password" id="password" value={password} placeholder={t("placeholder.password")} onChange={(e) => setPassword(e.target.value)}/>
                        </div>

                        {/* <%--Dont have an account? Sign up--%> */}
                        <div className="form-group mt-3">
                            <div>
                                <p className="text-black">{t("withoutAccount")}
                                    <a className="text-primary" href="/credentials/signin"> {t("register.register")}</a>
                                </p>
                            </div>
                            
                        </div>

                        {/* <%--Submit--%> */}
                        <div className="form-group mt-3 d-flex justify-content-center">
                            <button className="btn btn-light" type="submit">{t("back")}</button>
                            <button onClick={()=>login(email, password)} className="btn btn-primary" type="submit">{t("logIn")}</button>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    )
}


export default LoginPage;