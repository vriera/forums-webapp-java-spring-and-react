import React from "react";
import { useTranslation } from "react-i18next";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';
import { User } from "./../models/UserTypes"

const SigninPane = (props: {updateOptionCallback: any}) => {
    const { t } = useTranslation(); 
    
    const user: User = {} as User; //This is mocking an user to save the information and should be passed to the api call

    const [email, setEmail] = React.useState("");
    const [name, setName] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [repeatPassword, setRepeatPassword] = React.useState("");


    
    function signinUser(email: string, username: string, password: string, repeatPassword: string) {
        user.email = email;
        user.username = username;
        if(password === repeatPassword) {
            user.password = password;
        }
    }

    return (
        <div className="container">
            <div className="white-pill">
                <div className="d-flex justify-content-center">
                    <div className="h1 text-primary">{t("register.register")}</div>
                </div>
                <hr/>

                <div>
                    {/* <%--Email--%> */}
                    <div className="form-group mt-3">
                        <p className="text-black">{t("email")}</p>
                        <input className="form-control" type="email" id="email" value={email} placeholder={t("placeholder.email")} onChange={(e) => setEmail(e.target.value)} />
                    </div>
                </div>

                {/* <%--Username--%> */}
                <div className="form-group mt-3">
                    <p className="text-black">{t("username")}</p>
                    <input className="form-control" type="text" id="username" value={name} placeholder={t("placeholder.username")} onChange={(e) => setName(e.target.value)} />
                </div>

                {/* <%--Password--%> */}
                <div className="form-group mt-3">
                    <p className="text-black">{t("password.message")}</p>
                    <input className="form-control" type="password" id="password" value={password} placeholder={t("placeholder.password")} onChange={(e) => setPassword(e.target.value)}/>
                </div>

                {/* <%--Repeat password--%> */}
                <div className="form-group mt-3">
                    <p className="text-black">{t("password.repeat")}</p>
                    <input className="form-control" type="password" id="repeatPassword" value={repeatPassword} placeholder={t("password.repeat")} onChange={(e) => setRepeatPassword(e.target.value)}/>
                </div>

                {/* <%--Already have an account? Sign in--%> */}
                <div className="form-group mt-3">
                    <p className="text-black">{t("register.question")}
                        <a onClick={props.updateOptionCallback} className="text-primary"> {t("register.login")}</a>
                    </p>
                </div>

                {/* <%--Sign in button--%> */}
                <div className="form-group mt-3 d-flex justify-content-center">
                    <button className="btn btn-light" type="submit">{t("back")}</button>
                    <button onClick={()=>signinUser(email, name, password, repeatPassword)} className="btn btn-primary" type="submit">{t("register.register")}</button>
                </div>

            </div>

        </div>
    );
}

export default SigninPane;