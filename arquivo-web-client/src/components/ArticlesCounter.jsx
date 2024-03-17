import { useEffect, useState } from "react";

function ArticlesCounter({ entityId }) {
    const [counter, setPosts] = useState([]);

    useEffect(() => {
        fetch(import.meta.env.VITE_REST_URL +"/article/count/" + entityId)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    return (
        <span>{counter}</span>
    );

};

export default ArticlesCounter;