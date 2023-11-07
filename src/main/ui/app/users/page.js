'use client'
import axios from "axios";
import { useState } from "react";


export default function() {
    //grab the list of users from my endpoint
    const [users, setUsers] = useState([]);

    axios.get('http://localhost:8080/simplest/api/users/')
        .then( (response) => {
            //response has "success" which is an array with one object.
            const data = response.success[0].data;
            setUsers(data.users);
            console.log(response);

        }).catch( (error) => {
            console.log(error);
        });

    return (
        <ol>
        {
            users.map( (idx) => {
                return <li>{idx}</li>
            })
        }
        </ol>
    );
}
