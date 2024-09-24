import Link from "next/link";
import { HomeIcon, LightBulbIcon, UsersIcon } from '@heroicons/react/24/outline';
import styles from './css/topnav.module.css'
import { Button } from "@mui/material";

export default function TopNav({toggleTheme}) {
  return (
    <div className={styles.navOuter}>
      <Link className={`${styles.navItem} ${styles.left}`} href="/"> 
        <HomeIcon className={styles.Icon} />
      </Link>
      <Button variant="contained" onClick={toggleTheme}>
        <LightBulbIcon />
      </Button>
      <Link className={styles.navItem} href="/users">
        <UsersIcon className={styles.icon} />
      </Link>
    </div>
  );
};
