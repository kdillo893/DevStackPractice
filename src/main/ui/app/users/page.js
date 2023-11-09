"use client";
import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@mui/material";
import axios from "axios";
import { useEffect, useState } from "react";

function UserRow({ uid, first_name, last_name, email}) {

    return (
        <TableRow>
            <TableCell>{uid}</TableCell>
            <TableCell>{first_name}</TableCell>
            <TableCell>{last_name}</TableCell>
            <TableCell>{email}</TableCell>
            <TableCell>Notes</TableCell>
        </TableRow>
    )
}

export default function() {
    const [users, setUsers] = useState([]);
    const [usersLoaded, setUsersLoaded] = useState(false);

    //this happens on initial load (basically componentDidMount)
    useEffect(() => {
        if (users == null || users.length == 0)
        axios.get('/api/users/')
            .then( (response) => {
                //response has "success" which is an array with one object.
                const data = response.data.success[0].data;

                if (data != null) setUsers(data.users);

                setUsersLoaded(true);
            }).catch( (error) => {
                console.log(error);
            });
    });


    if (!usersLoaded) return <p>Loading ...</p>;
    if (!users || users.length == 0) return <p>No users</p>;

    return (
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>UID</TableCell>
                        <TableCell>First Name</TableCell>
                        <TableCell>Last Name</TableCell>
                        <TableCell>Email</TableCell>
                        <TableCell>Notes</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        users?.map( (aUser, idx) => {
                            return <UserRow key={idx} {... aUser} />
                        })
                    }
                </TableBody>
            </Table>
        </TableContainer>
    );
}
