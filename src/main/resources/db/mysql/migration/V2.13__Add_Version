ALTER TABLE `logs` ADD COLUMN `version`  INT(11) NOT NULL;
UPDATE `logs` as s,(SELECT id,(CASE entry_id WHEN @curType THEN @curRow := @curRow + 1 ELSE @curRow := 1 AND @curType := entry_id  END)
        AS RANK FROM `logs` p, (SELECT @curRow := 0, @curType := '') r ORDER BY  modified ASC) AS t SET s.version =t.rank  WHERE s.id = t.id ;
